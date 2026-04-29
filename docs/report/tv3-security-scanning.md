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
```

> Pipeline sẽ dừng lại (FAIL) ngay tại stage này nếu Gitleaks phát hiện secret bị lộ.

### 1.3 Xử Lý False Positive

Sau lần chạy đầu tiên, Gitleaks phát hiện **13 findings** trong lịch sử commit — tất cả đều là Keycloak client secret từ **upstream repository** (nashtech-garage/yas), không phải secret thật của nhóm. Các giá trị này là credential cố định dùng cho môi trường dev/demo.

Xử lý bằng cách thêm `commits` vào `allowlist` trong `gitleaks.toml`:

```toml
[allowlist]
description = "global allow list"
paths = [
  '''test-realm.json''',
  '''realm-export''',
  '''keycloak-yas-realm-import.yaml''',
  '''target'''
]
# False positives from upstream YAS repository (dev/demo Keycloak credentials, not real secrets)
commits = [
  "af2c9421030761ec4eccb0994a6c576592be113b",
  "8dc5e08456e8fa8c970b7b0cadcffdcd15d77e39",
  "f3d6c4a83259ca06fc9bc1889b9369d11b423256",
  "2192b03da8ca22e188ecf17f693fb9fbe9376811",
  "b2294a232aa1eea10b9814cf6ece03c5871b98d7",
  "e8fb3139974eb1a27b224416742c6902300ffee3",
  "0cac9558db4e4aa004d72e47e652384d6f32a666",
  "14f0528e9d235c13db92db3bf5e9f3b1cf5b1a7e"
]
```

Sử dụng `commits` allowlist thay vì `paths` để đảm bảo chính xác — chỉ bỏ qua đúng các commit từ upstream, không bỏ qua các secret thật có thể xuất hiện trong tương lai tại cùng file đó.

### 1.4 Hình Ảnh Minh Chứng

**Hình 1.1 — Stage Secret Scanning chạy thành công (không phát hiện secret)**

![Gitleaks Success](../images/tv3/gitleak_success.png)

**Hình 1.2 — Pipeline thất bại khi Gitleaks phát hiện secret**

![Gitleaks Failed](../images/tv3/gitleaks_failed.png)

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
| Phương thức xác thực | API Token (lưu trong Jenkins Credentials) |
| Phương thức tích hợp | Snyk CLI |

### 3.2 Cấu Hình Stage Trong Jenkinsfile

```groovy
stage('Dependency Scan') {
    steps {
        withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
            sh 'snyk auth $SNYK_TOKEN'
            sh 'snyk test --all-projects --json > snyk-report.json || true'
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'snyk-report.json', allowEmptyArchive: true
        }
    }
}
```

> Sử dụng `|| true` để pipeline không dừng lại khi Snyk tìm thấy vulnerability có mức độ thấp. Có thể điều chỉnh ngưỡng với flag `--severity-threshold=high`.

### 3.3 Hình Ảnh Minh Chứng

**Hình 3.1 — Snyk Dashboard: danh sách vulnerability được phát hiện**

```
[HÌNH: https://app.snyk.io > Projects > yas — danh sách các dependency có lỗ hổng]
```

**Hình 3.2 — Stage Dependency Scan trong Jenkins: output kết quả quét**

```
[HÌNH: Jenkins Console Output của lệnh snyk test với kết quả]
```

---

## 4. Tổng Hợp Pipeline Sau Khi Tích Hợp Security Stages

**Hình 4.1 — Toàn bộ pipeline bao gồm các stage quét bảo mật**

```
[HÌNH: Blue Ocean hoặc Stage View thể hiện đầy đủ: Secret Scanning, Code Quality, Quality Gate, Dependency Scan]
```

---

## 5. Vấn Đề Gặp Phải Và Cách Giải Quyết

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| [Điền vào] | | |

---

*Phần này do TV3 thực hiện và chịu trách nhiệm nội dung.*
