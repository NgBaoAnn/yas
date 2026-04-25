# Phần 2: Branch Protection và Unit Test (media, product)

**Người thực hiện:** [Họ và tên] — MSSV: `XXXXXXXX`  
**Phạm vi:** Cấu hình Branch Protection trên GitHub, viết unit test cho service `media` và `product`, tạo Pull Request demo.

---

## 1. Cấu Hình Branch Protection Trên GitHub

### 1.1 Các Rule Đã Cấu Hình

Cấu hình tại: `GitHub Repository > Settings > Branches > Add branch protection rule`

| Rule | Giá trị | Mục đích |
|------|:-------:|----------|
| Branch name pattern | `main` | Áp dụng cho nhánh chính |
| Require a pull request before merging | Bật | Bắt buộc tạo PR, cấm push trực tiếp |
| Required number of approvals | `2` | Cần ít nhất 2 thành viên approve |
| Require status checks to pass | Bật | Jenkins CI phải pass trước khi merge |
| Require branches to be up to date before merging | Bật | Branch phải được sync với `main` |
| Do not allow bypassing above settings | Bật | Admin cũng phải tuân thủ quy tắc |

### 1.2 Hình Ảnh Minh Chứng

**Hình 1.1 — Trang cấu hình Branch Protection Rules trên GitHub**

```
[HÌNH: GitHub > Repository > Settings > Branches > Protection rules cho nhánh main]
```

**Hình 1.2 — Push trực tiếp vào nhánh `main` bị từ chối**

```
[HÌNH: Terminal output với thông báo "remote: error: GH006: Protected branch update failed"]
```

**Hình 1.3 — Pull Request hiển thị yêu cầu 2 lượt approve và CI check phải pass**

```
[HÌNH: Trang PR trên GitHub với phần "Review required" và "Checks" đang chờ]
```

---

## 2. Unit Test — Service `media`

### 2.1 Thông Tin Branch Và Pull Request

| Thông tin | Giá trị |
|-----------|---------|
| Tên branch | `test/media` |
| Branch gốc | `main` |
| Link PR | `https://github.com/<ten-nhom>/yas/pull/<so>` |

### 2.2 Danh Sách File Test

| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `MediaControllerTest.java` | `MediaController` — tất cả endpoints | 8 |
| `MediaServiceUnitTest.java` | `MediaService` — logic nghiệp vụ | 13 |
| `StringUtilsTest.java` | `StringUtils` — tiện ích xử lý chuỗi | 7 |
| `FileTypeValidatorTest.java` | `FileTypeValidator` — xác thực loại file | 6 |
| **Tổng** | | **34+** |

### 2.3 Lệnh Chạy Test

Do project sử dụng cấu trúc monorepo với thuộc tính `${revision}`, lệnh phải chạy từ bên trong thư mục `media/`:

```bash
cd /duong-dan/yas/media
./mvnw -f ../pom.xml test -pl media -am
```

Để sinh báo cáo JaCoCo:

```bash
./mvnw -f ../pom.xml test jacoco:report -pl media -am
open target/site/jacoco/index.html
```

### 2.4 Kết Quả Coverage (media)

| Package | Coverage (Instructions) | Coverage (Branches) |
|---------|:-----------------------:|:-------------------:|
| `controller` | % | % |
| `service` | % | % |
| `utils` | % | % |
| `repository` | % | % |
| **Tổng** | **%** | **%** |

Yêu cầu tối thiểu: >= 70%

### 2.5 Hình Ảnh Minh Chứng

**Hình 2.1 — Kết quả chạy test: tất cả test case PASS**

```
[HÌNH: Terminal output "Tests run: XX, Failures: 0, Errors: 0 — BUILD SUCCESS"]
```

**Hình 2.2 — Báo cáo JaCoCo Coverage cho service media**

```
[HÌNH: Trình duyệt mở file media/target/site/jacoco/index.html hiển thị tổng coverage]
```

**Hình 2.3 — Pull Request `test/media` trên GitHub**

```
[HÌNH: Trang PR với danh sách file test được thêm vào và trạng thái CI]
```

---

## 3. Unit Test — Service `product`

### 3.1 Thông Tin Branch Và Pull Request

| Thông tin | Giá trị |
|-----------|---------|
| Tên branch | `test/product` |
| Branch gốc | `main` |
| Link PR | `https://github.com/<ten-nhom>/yas/pull/<so>` |

### 3.2 Danh Sách File Test

| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| (Có sẵn) `BrandControllerTest.java` | `BrandController` | |
| (Có sẵn) `CategoryControllerTest.java` | `CategoryController` | |
| (Bổ sung) [tên file] | [tên lớp] | |
| **Tổng** | | |

### 3.3 Kết Quả Coverage (product)

| Package | Coverage (Instructions) | Coverage (Branches) |
|---------|:-----------------------:|:-------------------:|
| `controller` | % | % |
| `service` | % | % |
| **Tổng** | **%** | **%** |

Yêu cầu tối thiểu: >= 70%

### 3.4 Hình Ảnh Minh Chứng

**Hình 3.1 — Kết quả chạy test service product: BUILD SUCCESS**

```
[HÌNH: Terminal output BUILD SUCCESS cho module product]
```

**Hình 3.2 — Báo cáo JaCoCo Coverage cho service product**

```
[HÌNH: product/target/site/jacoco/index.html]
```

---

## 4. Pull Request Demo (Trạng Thái Open)

Theo yêu cầu nộp bài, nhóm duy trì ít nhất một PR ở trạng thái Open trên GitHub.

| Thông tin | Giá trị |
|-----------|---------|
| Tiêu đề PR | `test(media): add unit tests for MediaController and utils` |
| Trạng thái | Open |
| Reviewer được gán | [Tên TV khác], [Tên TV khác] |
| Trạng thái CI | Passing |

**Hình 4.1 — Pull Request đang ở trạng thái Open, chờ review**

```
[HÌNH: Trang PR trên GitHub với nhãn "Open", hiển thị reviewer và CI status]
```

---

## 5. Vấn Đề Gặp Phải Và Cách Giải Quyết

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| Lệnh `./mvnw test` báo lỗi `${revision} not found` | Chạy Maven từ sai thư mục, không đọc được root POM | Chạy `./mvnw -f ../pom.xml test -pl media -am` từ bên trong thư mục `media/` |
| `@WebMvcTest` lỗi khi load ApplicationContext | OAuth2 tự động cấu hình gây xung đột trong môi trường test | Thêm `excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class` |
| [Điền thêm nếu có] | | |

---

*Phần này do TV2 thực hiện và chịu trách nhiệm nội dung.*
