pipeline {
    agent { label 'individual-agent' }

    stages {
        stage('Pre-check') {
            steps {
                script {
                    echo "Checking environment..."
                    sh 'java -version'
                    sh 'mvn -version'
                    sh 'gitleaks version'
                }
            }
        }
        stage('Secret Scanning') {
            steps {
                sh '''
                    gitleaks detect --source . \
                        --config gitleaks.toml \
                        --report-format json \
                        --report-path gitleaks-report.json \
                        --exit-code 1
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json',
                                     allowEmptyArchive: true
                }
            }
        }
        stage('Monorepo Execution') {
            steps {
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1 HEAD', returnStdout: true).trim().split('\n')
                    def services = ['media', 'product', 'order', 'inventory', 'cart', 'customer'] 

                    for (service in services) {
                        if (changedFiles.any { it.startsWith("${service}/") }) {
                            echo "Processing service: ${service}"
                            sh "mvn test -pl ${service} -am"
                            junit testResults: "${service}/target/surefire-reports/*.xml", allowEmptyResults: true
                            sh "mvn package -DskipTests -pl ${service} -am"
                        } else {
                            echo "${service} no change, skip."
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline Succeeded"
        }
        failure {
            echo "❌ Pipeline Failed"
        }
        always {
            cleanWs()
        }
    }
}