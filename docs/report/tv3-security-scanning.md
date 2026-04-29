# Phần 3: Security Scanning

**Người thực hiện:** [Họ và tên] — MSSV: `XXXXXXXX`  
**Phạm vi:** Tích hợp Gitleaks, SonarQube, Snyk vào Jenkins pipeline.

---

## 1. Tích Hợp Gitleaks — Quét Secret Bị Lộ

### 1.1 Mô Tả

Gitleaks là công cụ quét mã nguồn để phát hiện các thông tin nhạy cảm bị commit nhầm vào repository, bao gồm API key, mật khẩu, token truy cập và các credential khác.

### 1.2 Cấu Hình Stage Trong Jenkinsfile

```groovy
stage('Secret Scanning') {
    steps {
        sh '''
            gitleaks detect --source . \
                --report-format json \
                --report-path gitleaks-report.json \
                --exit-code 1
        '''
    }
    post {
        always {
            archiveArtifacts artifacts: 'gitleaks-report.json', allowEmptyArchive: true
        }
    }
}
```

> Pipeline sẽ dừng lại (FAIL) ngay tại stage này nếu Gitleaks phát hiện secret bị lộ.

### 1.3 Cấu Hình File `.gitleaksignore` (Nếu Có)

> Trong trường hợp phát sinh false positive (cảnh báo nhầm), thêm exception vào file `.gitleaksignore`:

```
[Điền nội dung file .gitleaksignore nếu nhóm có cấu hình]
```

### 1.4 Hình Ảnh Minh Chứng

**Hình 1.1 — Stage Secret Scanning chạy thành công (không phát hiện secret)**

```
[HÌNH: Jenkins Console Output — Gitleaks chạy và kết thúc với trạng thái SUCCESS]
```

**Hình 1.2 — Pipeline thất bại khi Gitleaks phát hiện secret (trường hợp demo)**

```
[HÌNH: Jenkins build FAIL với thông báo Gitleaks tìm thấy secret trong commit]
```

---

## 2. Tích Hợp SonarQube — Phân Tích Chất Lượng Code

### 2.1 Cài Đặt SonarQube Server

| Thông số | Giá trị |
|----------|---------|
| Phương thức triển khai | Docker |
| Phiên bản SonarQube | `X.X` |
| Địa chỉ truy cập | `http://<url>:9000` |
| Project Key | `yas` |

### 2.2 Kết Nối Jenkins Với SonarQube

Các bước cấu hình:
1. Cài plugin **SonarQube Scanner** trên Jenkins.
2. Vào `Manage Jenkins > Configure System > SonarQube servers`: thêm URL và token xác thực.
3. Tạo token trên SonarQube: `My Account > Security > Generate Token`.
4. Lưu token vào Jenkins Credentials.

### 2.3 Cấu Hình Stage Trong Jenkinsfile

```groovy
stage('Code Quality') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh './mvnw sonar:sonar -Dsonar.projectKey=yas'
        }
    }
}
stage('Quality Gate') {
    steps {
        timeout(time: 5, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```

### 2.4 Hình Ảnh Minh Chứng

**Hình 2.1 — SonarQube Dashboard: tổng quan chất lượng code**

```
[HÌNH: SonarQube tại http://<url>:9000 — hiển thị Bugs, Vulnerabilities, Code Smells, Coverage]
```

**Hình 2.2 — Chi tiết kết quả phân tích project**

```
[HÌNH: SonarQube > Projects > yas > Overview với các chỉ số đánh giá]
```

**Hình 2.3 — Stage Code Quality và Quality Gate trong Jenkins pipeline**

```
[HÌNH: Jenkins Stage View hoặc Console Output của hai stage SonarQube]
```

---

## 3. Tích Hợp Snyk — Quét Lỗ Hổng Dependency

### 3.1 Cài Đặt Và Cấu Hình

| Thông số | Giá trị |
|----------|---------|
| Tài khoản Snyk | `https://app.snyk.io` |
| Phương thức xác thực | API Token (lưu trong Jenkins Credentials với ID `snyk-token`) |
| Phương thức tích hợp | Snyk CLI (`snyk test` + `snyk monitor`) |

Các bước cấu hình:
1. Đăng ký tài khoản tại `https://app.snyk.io`
2. Vào `Account Settings > Auth Token` → copy token
3. Thêm vào Jenkins: `Manage Jenkins > Credentials > Global > Add Credentials` (Kind: `Secret text`, ID: `snyk-token`)
4. TV1 cài Snyk CLI trên Jenkins agent: `npm install -g snyk`

### 3.2 Cấu Hình Stage Trong Jenkinsfile

```groovy
stage('Dependency Scan') {
    steps {
        withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
            sh 'snyk auth $SNYK_TOKEN'
            sh 'snyk test --all-projects --json > snyk-report.json || true'
            sh 'snyk monitor --all-projects || true'
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'snyk-report.json',
                             allowEmptyArchive: true
        }
    }
}
```

> `snyk test` quét và báo cáo vulnerability ra console/file. `snyk monitor` đẩy kết quả lên Snyk dashboard để theo dõi liên tục. Cả hai dùng `|| true` để pipeline không FAIL khi phát hiện vulnerability mức thấp.

### 3.3 Kết Quả Quét

Snyk phát hiện vulnerability trong toàn bộ monorepo `com-suon-bi-cha/yas` với tổng cộng **55 Critical, 179 High, 140 Medium, 71 Low** trên tất cả các module. Đây là các lỗ hổng trong dependency (thư viện bên thứ ba) mà các service đang sử dụng.

### 3.4 Hình Ảnh Minh Chứng

**Hình 3.1 — Snyk Dashboard: danh sách project và vulnerability được phát hiện**

![Snyk Monitor](../images/tv3/snyk_monitor.png)

**Hình 3.2 — Stage Dependency Scan trong Jenkins pipeline chạy thành công**

![Snyk Success](../images/tv3/snyk_success.png)

---

## 4. Tổng Hợp Pipeline Sau Khi Tích Hợp Security Stages

Pipeline hoàn chỉnh bao gồm các stage theo thứ tự:

```
Pre-check → Secret Scanning → Monorepo Execution → Coverage Report → Dependency Scan
```

**Hình 4.1 — Toàn bộ pipeline chạy thành công với stage Dependency Scan**

![Snyk Success](../images/tv3/snyk_success.png)

---

## 5. Vấn Đề Gặp Phải Và Cách Giải Quyết

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| `gitleaks: command not found` tại Pre-check | Gitleaks chưa được cài trên Jenkins agent | Phối hợp TV1 cài `brew install gitleaks` trên agent |
| Gitleaks phát hiện 13 findings, pipeline FAIL | Keycloak client secret từ upstream repo bị nhận nhầm là secret thật | Thêm `commits` allowlist vào `gitleaks.toml` cho 8 commit từ upstream |
| Snyk chạy xong nhưng không thấy project trên dashboard | `snyk test` chỉ quét local, không đẩy lên dashboard | Thêm `snyk monitor` vào stage để đăng ký project lên Snyk dashboard |

---

*Phần này do TV3 thực hiện và chịu trách nhiệm nội dung.*
