# 📋 Bảng Phân Chia Công Việc — Đồ Án 1: Triển Khai Hệ Thống CI

> **Công cụ CI/CD:** Jenkins (đã chốt)
> **Repository:** Đã fork thành công về GitHub của nhóm
> **Thời gian thực hiện:** 9 ngày (25/04 → 03/05/2026) | **Deadline:** 03/05/2026
> **Số thành viên:** 4 người

---

## Nguyên Tắc Phân Công

- Mỗi người phụ trách **1 mảng độc lập**, tự chịu trách nhiệm hoàn thành từ đầu đến cuối.
- Không có vai trò Owner/Support — ai làm phần nấy.
- **Mỗi người tự screenshot và viết báo cáo cho phần mình làm.** Cuối cùng ghép lại thành 1 file `.docx` duy nhất.
- Các phần có **thứ tự phụ thuộc**: TV1 cần hoàn thành hạ tầng Jenkins trước để các TV khác có thể tích hợp stage của mình vào pipeline.

---

## Tổng Quan 4 Phần Độc Lập

| Thành viên | Mảng phụ trách | Tóm tắt |
|:----------:|----------------|---------|
| **TV1** | 🏗️ Jenkins Infrastructure & Pipeline | Setup Jenkins server, Webhook, Multibranch Pipeline, viết Jenkinsfile với logic Monorepo |
| **TV2** | 🔒 Branch Protection & Unit Test (`media`, `product`) | Cấu hình Branch Protection trên GitHub, viết unit test cho 2 service |
| **TV3** | 🛡️ Security Scanning & Unit Test (`cart`) | Tích hợp Gitleaks, SonarQube, Snyk vào pipeline + viết unit test cho 1 service |
| **TV4** | 📊 Coverage Gate & Unit Test (`order`) | Cấu hình JaCoCo ≥ 70%, viết unit test cho 1 service |
| **ALL** | 📝 Documentation | Mỗi người tự screenshot + viết báo cáo cho phần mình → ghép thành 1 file `.docx` |

---

## Chi Tiết Từng Phần

### 🏗️ TV1 — Jenkins Infrastructure & Pipeline

> **Phạm vi:** Toàn bộ hạ tầng Jenkins và logic pipeline cốt lõi.
> **Ưu tiên hoàn thành sớm** vì các TV khác phụ thuộc vào Jenkinsfile để thêm stage.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cài đặt Jenkins Server | Triển khai Jenkins bằng Docker. Cài plugin: Git, Pipeline, Multibranch Pipeline, JaCoCo, Warnings NG. | Jenkins dashboard hoạt động |
| 2 | Cấu hình Webhook GitHub → Jenkins | Tạo Webhook trên GitHub trỏ về Jenkins (dùng ngrok/cloudflare tunnel nếu chạy local). Cấu hình credential (PAT hoặc SSH key). | Push event trigger Jenkins build |
| 3 | Thiết lập Multibranch Pipeline | Tạo Multibranch Pipeline Job, cấu hình Branch Source từ GitHub repo. Jenkins tự quét và nhận diện branch mới. | Jenkins scan được tất cả branch |
| 4 | Viết Jenkinsfile (Test → Build) | Viết `Jenkinsfile` ở root repo với 2 stage cơ bản: **Test** (`mvn test` + JUnit publisher) và **Build** (`mvn package -DskipTests`). | Pipeline chạy thành công |
| 5 | Cấu hình Monorepo Change Detection | Trong `Jenkinsfile`, dùng `git diff` để detect thư mục thay đổi → chỉ build/test service tương ứng. | Push vào `media/` chỉ trigger build `media` |
| 6 | Kiểm thử end-to-end | Tạo feature branch, push code, verify pipeline tự kích hoạt đúng service. | Demo luồng hoạt động |
| 📝 | Screenshot & viết báo cáo phần mình | Chụp ảnh: Jenkins setup, Webhook config, Multibranch job, Jenkinsfile stages, Monorepo demo. Viết mô tả kèm ảnh. | File báo cáo phần Jenkins Infrastructure |

---

### 🔒 TV2 — Branch Protection & Unit Test (`media`, `product`)

> **Phạm vi:** Cấu hình bảo vệ nhánh trên GitHub + viết unit test cho 2 service.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cấu hình GitHub Branch Protection | Settings → Branches → Protection rules cho `main`: ✅ Require PR, ✅ Require 2 approvals, ✅ Require status checks (Jenkins), ✅ Block direct push. | Push trực tiếp vào `main` bị reject |
| 2 | Viết Unit Test — `media` service | Tạo branch `test/media`. Viết unit test cho các lớp Service/Controller chính. Đảm bảo coverage ≥ 70%. | PR với unit test cho `media` |
| 3 | Viết Unit Test — `product` service | Tạo branch `test/product`. Viết unit test tương tự. Đảm bảo coverage ≥ 70%. | PR với unit test cho `product` |
| 4 | Tạo PR demo (trạng thái Open) | Tạo ít nhất 1 PR vào `main` ở trạng thái **Open** để minh chứng luồng làm việc (yêu cầu nộp bài). | 1 PR Open trên GitHub |
| 📝 | Screenshot & viết báo cáo phần mình | Chụp ảnh: Branch Protection settings, PR demo với 2 reviewer + CI check, test coverage report cho media & product. Viết mô tả kèm ảnh. | File báo cáo phần Branch Protection & Unit Test |

---

### 🛡️ TV3 — Security Scanning & Unit Test (`cart`)

> **Phạm vi:** Tích hợp 3 công cụ quét bảo mật/chất lượng code vào Jenkins pipeline + viết unit test cho 1 service.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Tích hợp Gitleaks | Thêm stage `Secret Scanning` trong Jenkinsfile chạy Gitleaks. Cấu hình `.gitleaksignore` cho false positive. Pipeline FAIL nếu phát hiện secret bị leak. | Stage Gitleaks hoạt động |
| 2 | Tích hợp SonarQube | Deploy SonarQube server (Docker). Cài SonarQube Scanner plugin trên Jenkins. Thêm stage `Code Quality` trong Jenkinsfile. | SonarQube dashboard hiển thị kết quả |
| 3 | Tích hợp Snyk | Đăng ký Snyk account, lấy API token. Thêm stage `Dependency Scan` trong Jenkinsfile chạy `snyk test`. | Snyk report trong Jenkins |
| 4 | Viết Unit Test — `cart` service | Tạo branch `test/cart`. Viết unit test cho các lớp chính. Đảm bảo coverage ≥ 70%. | PR với unit test cho `cart` |
| 📝 | Screenshot & viết báo cáo phần mình | Chụp ảnh: Gitleaks stage output, SonarQube dashboard, Snyk report, test coverage cho cart. Viết mô tả kèm ảnh. | File báo cáo phần Security Scanning & Unit Test |

---

### 📊 TV4 — Coverage Gate & Unit Test (`order`)

> **Phạm vi:** Cấu hình chặn pipeline khi coverage thấp, viết unit test cho 1 service.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cấu hình JaCoCo + Coverage Gate | Thêm JaCoCo plugin vào `pom.xml`. Thêm stage `Coverage Report` trong Jenkinsfile, publish JaCoCo report. Pipeline **FAIL** nếu coverage < 70%. | Pipeline tự fail khi coverage < 70% |
| 2 | Viết Unit Test — `order` service | Tạo branch `test/order`. Viết unit test cho các lớp chính. Đảm bảo coverage ≥ 70%. | PR với unit test cho `order` |
| 📝 | Screenshot & viết báo cáo phần mình | Chụp ảnh: JaCoCo config, Coverage Gate pass/fail demo, test coverage cho order. Viết mô tả kèm ảnh. | File báo cáo phần Coverage Gate & Unit Test |

---

### 📝 Quy Trình Documentation (ALL — Cả 4 thành viên)

> **Nguyên tắc:** Ai cài đặt phần nào thì **tự screenshot và viết báo cáo phần đó**. Cuối cùng ghép lại.

| Bước | Mô tả | Thời điểm |
|:----:|-------|:---------:|
| 1 | **Mỗi người** screenshot từng bước cài đặt/cấu hình ngay khi làm. Lưu vào thư mục `screenshots/<tên-mảng>/` | Trong lúc làm |
| 2 | **Mỗi người** viết mô tả cho phần mình (có thể dùng Google Docs hoặc file `.md` riêng) | Tuần 2-3 |
| 3 | **TV4** tổng hợp 4 phần thành 1 file `.docx` duy nhất, thêm link repo + format đúng quy định | Tuần 3 |
| 4 | **Cả nhóm** review báo cáo lần cuối trước khi nộp | Trước deadline |

**Cấu trúc thư mục screenshot đề xuất:**
```
screenshots/
├── jenkins-infra/        ← TV1: Jenkins setup, Webhook, Multibranch, Jenkinsfile
├── branch-protection/    ← TV2: GitHub settings, PR demo, test coverage media/product
├── security-scanning/    ← TV3: Gitleaks, SonarQube dashboard, Snyk report, cart tests
└── coverage-gate/        ← TV4: JaCoCo config, Coverage Gate demo, order tests
```

**Đặt tên file báo cáo:** `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` (MSSV sắp xếp tăng dần)

---

## Timeline Tổng Hợp (9 ngày)

| Giai đoạn | Ngày | Nội dung |
|:---------:|:----:|----------|
| **Phase 1** | 25/04 – 27/04 (3 ngày) | Hạ tầng Jenkins & Branch Protection |
| **Phase 2** | 28/04 – 30/04 (3 ngày) | Unit Test, Coverage Gate & Security Tools |
| **Phase 3** | 01/05 – 03/05 (3 ngày) | Hoàn thiện, viết docs & nộp bài |

```
       Phase 1 (25-27/04)       Phase 2 (28-30/04)       Phase 3 (01-03/05)
  ──────────────────────   ──────────────────────   ──────────────────────

  TV1 ████████████████████ ░░░░░░░░░░░░░░░░░░░░░░ ░░░░░░░░░░░░░░░░░░░░░░
      Jenkins + Webhook     (hoàn thành)            📝 viết docs phần mình
      Multibranch Pipeline
      Jenkinsfile + Monorepo

  TV2 ████████████░░░░░░░░ ████████████████████░░ ░░░░░░░░░░░░░░░░░░░░░░
      Branch Protection     Unit Test media          📝 viết docs phần mình
                            Unit Test product        + tạo PR demo

  TV3 ░░░░░░░░░░░░░░░░░░░░ ████████████████████░░ ████████████░░░░░░░░░░
      (chờ Jenkinsfile)     Gitleaks + SonarQube     Snyk + Unit Test cart
                                                     📝 viết docs phần mình

  TV4 ░░░░░░░░░░░░░░░░░░░░ ████████████████████░░ ████████████████████░░
      (chờ Jenkinsfile)     JaCoCo + Coverage Gate   Unit Test order
                                                     📝 viết docs phần mình
                                                     + ghép file .docx
  ──────────────────────   ──────────────────────   ──────────────────────
                                                      03/05 ⏰ Deadline
```

---

## Thứ Tự Phụ Thuộc

```
TV1: Jenkins + Jenkinsfile + Monorepo
         │
         ▼ (Jenkinsfile sẵn sàng)
    ┌────┴─────────────┐
    ▼                  ▼
   TV3                TV4
   Thêm stage         Thêm stage
   Security Scan      Coverage Gate
    │                  │
    ▼                  ▼
   TV2: Branch Protection bật Status Check
         │
         ▼
   Tất cả PR phải pass pipeline mới merge được
```

> **Lưu ý:** TV1 cần hoàn thành Jenkinsfile cơ bản trong **Phase 1 (trước 28/04)** để TV3 và TV4 có thể thêm stage của mình vào từ **Phase 2**. TV2 có thể làm Branch Protection song song nhưng nên bật Status Check sau khi Jenkins pipeline đã ổn định.
