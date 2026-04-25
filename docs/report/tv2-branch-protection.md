# Phần 2: Branch Protection và Unit Test (media, product)

**Người thực hiện:** [Họ và tên] — MSSV: `XXXXXXXX`  
**Phạm vi:** Cấu hình Branch Protection trên GitHub, viết unit test cho service `media` và `product`, tạo Pull Request demo.

---

## 1. Cấu Hình Branch Protection Trên GitHub

> Phần này sẽ được hoàn thiện sau khi TV1 hoàn thành cấu hình Jenkins CI, vì Branch Protection cần Status Check từ Jenkins pipeline.

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
| `MediaControllerTest.java` | `MediaController` — tất cả 5 endpoints (GET, POST, DELETE, GET by IDs, GET file) | 8 |
| `MediaServiceUnitTest.java` | `MediaService` — logic nghiệp vụ (đã có sẵn) | 13 |
| `FileSystemRepositoryTest.java` | `FileSystemRepository` — thao tác lưu/đọc file | 4 |
| `StringUtilsTest.java` | `StringUtils` — xác thực chuỗi văn bản | 11 |
| `FileTypeValidatorTest.java` | `FileTypeValidator` — xác thực loại và nội dung file ảnh | 6 |
| **Tổng** | | **42** |

### 2.3 Lưu Ý Kỹ Thuật

Do project sử dụng cấu trúc monorepo với thuộc tính `${revision}` trong POM, Maven phải chạy từ bên trong thư mục `media/` (nơi có thư mục `.mvn/`) nhưng trỏ `-f` lên root `pom.xml`:

```bash
cd /duong-dan/yas/media

# Chạy test
./mvnw -f ../pom.xml test -pl media -am

# Sinh báo cáo JaCoCo
./mvnw -f ../pom.xml test jacoco:report -pl media -am

# Mở báo cáo
open target/site/jacoco/index.html
```

Ngoài ra, annotation `@WebMvcTest` cần loại trừ `OAuth2ResourceServerAutoConfiguration` để tránh lỗi load ApplicationContext trong môi trường test không có server OAuth2:

```java
@WebMvcTest(controllers = MediaController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest { ... }
```

### 2.4 Kết Quả Coverage (media)

| Package | Coverage (Instructions) | Coverage (Branches) |
|---------|:-----------------------:|:-------------------:|
| `com.yas.media.controller` | 100% | 100% |
| `com.yas.media.service` | 89% | 90% |
| `com.yas.media.utils` | 92% | 100% |
| `com.yas.media.viewmodel` | 86% | n/a |
| `com.yas.media.repository` | 73% | 62% |
| `com.yas.media.model` | 100% | n/a |
| `com.yas.media.mapper` | 38% | 7% |
| **Tổng** | **80%** | **65%** |

Yêu cầu tối thiểu: >= 70% — **Đạt**

### 2.5 Hình Ảnh Minh Chứng

**Hình 2.1 — Kết quả chạy test: 42 test case PASS, 0 Failures**

```
[HÌNH: Terminal output "Tests run: 42, Failures: 0, Errors: 0 — BUILD SUCCESS"]
```

**Hình 2.2 — Báo cáo JaCoCo Coverage cho service media (tổng 80%)**

![Báo cáo JaCoCo Coverage — media service](../screenshots/test/03-media-coverage-report.png)

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
| (Có sẵn và bổ sung) `ProductService*Test.java` (được tách thành 10 file nhỏ) | `ProductService` | Nhiều test case |
| (Có sẵn và bổ sung) `CategoryServiceTest.java` | `CategoryService` | Nhiều test case |
| (Bổ sung) `MediaServiceTest.java` | `MediaService` trong product | ~4 |
| (Bổ sung) `ProductConverterTest.java` | `ProductConverter` | Nhiều test case |
| **Tổng** | Các file trong `src/test/java` | **178** |

### 3.3 Kết Quả Coverage (product)

| Package | Coverage (Instructions) | Coverage (Branches) |
|---------|:-----------------------:|:-------------------:|
| `com.yas.product.controller` | 87% | 58% |
| `com.yas.product.service` | 64% | 46% |
| `com.yas.product.validation` | 93% | 50% |
| **Tổng** | **71%** | **47%** |

Yêu cầu tối thiểu: >= 70%

### 3.4 Hình Ảnh Minh Chứng

**Hình 3.1 — Kết quả chạy test service product: BUILD SUCCESS**

```
[INFO] Tests run: 178, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Hình 3.2 — Báo cáo JaCoCo Coverage cho service product (tổng 71%)**

![Báo cáo JaCoCo Coverage — product service](../screenshots/test/04-product-coverage-report.pngproduct-coverage.png)

---

## 4. Pull Request Demo (Trạng Thái Open)

Theo yêu cầu nộp bài, nhóm duy trì ít nhất một PR ở trạng thái Open trên GitHub.

| Thông tin | Giá trị |
|-----------|---------|
| Tiêu đề PR | `test(media): add unit tests for MediaController and utils` |
| Trạng thái | Open |
| Reviewer được gán | [Tên TV khác], [Tên TV khác] |
| Trạng thái CI | Pending (chờ Jenkins) |

**Hình 4.1 — Pull Request đang ở trạng thái Open, chờ review**

```
[HÌNH: Trang PR trên GitHub với nhãn "Open", hiển thị reviewer và CI status]
```

---

## 5. Vấn Đề Gặp Phải Và Cách Giải Quyết

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| Lệnh `./mvnw test` báo lỗi `${revision} not found` | Chạy Maven từ sai thư mục, không đọc được root POM | Chạy `./mvnw -f ../pom.xml test -pl media -am` từ bên trong thư mục `media/` |
| `@WebMvcTest` lỗi khi load ApplicationContext | OAuth2 tự động cấu hình gây xung đột trong môi trường test | Thêm `excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class` vào annotation `@WebMvcTest` |
| Test POST `/medias` trả về 400 thay vì 200 | Annotation `@ValidFileType` kiểm tra nội dung thực của file ảnh | Tạo ảnh PNG thật bằng `BufferedImage` + `ImageIO.write()` thay vì dùng byte giả |

---

*Phần này do TV2 thực hiện và chịu trách nhiệm nội dung.*
