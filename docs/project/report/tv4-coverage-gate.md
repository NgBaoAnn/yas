# Phần 4: Coverage Gate và Unit Test (6 modules)

**Người thực hiện:** [Họ và tên] — MSSV: `XXXXXXXX`  
**Phạm vi:** Cấu hình JaCoCo Coverage Gate (ngưỡng >= 70%), viết unit test cho 6 service module (customer, location, cart, tax, search, webhook), tổng hợp báo cáo cuối.

---

## 1. Cấu Hình JaCoCo Coverage Gate

### 1.1 Mô Tả

JaCoCo (Java Code Coverage) đo lường độ phủ của unit test trên mã nguồn. Coverage Gate là cơ chế tự động làm thất bại pipeline khi độ phủ thấp hơn ngưỡng quy định. Nhóm đặt ngưỡng là **70% instruction coverage**.

### 1.2 Cấu Hình Trong `pom.xml`

Thêm cấu hình `check` goal vào JaCoCo plugin để pipeline tự động fail khi coverage không đạt:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

> Khi coverage < 70%, lệnh `mvn verify` hoặc `mvn jacoco:check` sẽ trả về BUILD FAILURE.

### 1.3 Cấu Hình Stage Trong Jenkinsfile

```groovy
stage('Coverage Report') {
    steps {
        sh './mvnw -f ../pom.xml test jacoco:report -pl <service> -am'
    }
    post {
        always {
            jacoco(
                execPattern: '**/target/jacoco.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                minimumInstructionCoverage: '70',
                changeBuildStatus: true
            )
        }
    }
}
```

> Tham số `changeBuildStatus: true` cho phép plugin JaCoCo trên Jenkins tự động đổi trạng thái build sang FAILURE nếu coverage không đạt ngưỡng.

### 1.4 Hình Ảnh Minh Chứng

**Hình 1.1 — Cấu hình JaCoCo Coverage Gate trong file pom.xml**

![Hình 1.1](../assets/coverage-gate/jacoco-pom-config.png)

**Hình 1.2 — Pipeline thất bại khi coverage dưới ngưỡng 70% (trường hợp demo)**

![Hình 1.2](../assets/coverage-gate/pipeline-fail-below-70.png)

**Hình 1.3 — Pipeline thành công sau khi bổ sung đủ unit test**

![Hình 1.3](../assets/coverage-gate/pipeline-pass-above-70.png)

**Hình 1.4 — Báo cáo JaCoCo Coverage hiển thị trong giao diện Jenkins**

![Hình 1.4](../assets/coverage-gate/jacoco-jenkins-report.png)

---

## 2. Unit Test — Chi Tiết Từng Module

### 2.1 Hướng Dẫn Chung Chạy Test

Do project sử dụng cấu trúc monorepo với thuộc tính `${revision}`, lệnh phải chạy từ bên trong thư mục module tương ứng:

```bash
cd /duong-dan/yas/<module>
./mvnw -f ../pom.xml test -pl <module> -am
./mvnw -f ../pom.xml test jacoco:report -pl <module> -am
open target/site/jacoco/index.html
```

### 2.2 Module `customer`

- **Branch:** `test/customer`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `CustomerControllerTest.java` | `CustomerController` | 7 |
| `LocationControllerTest.java` | `LocationController` | 5 |
| `UserAddressControllerTest.java` | `UserAddressController` | 5 |
| `CustomerServiceTest.java` | `CustomerService` | 15 |
| `LocationServiceTest.java` | `LocationService` | 3 |
| `UserAddressServiceTest.java` | `UserAddressService` | 10 |
| `MessagesUtilsTest.java` | `MessagesUtils` | 2 |

**Kết Quả Coverage:** Instructions **87%** | Branches **87%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho customer](../assets/coverage/customer-coverage.png)

### 2.3 Module `location`

- **Branch:** `test/location`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `AddressControllerTest.java` | `AddressController` | 6 |
| `CountryControllerTest.java` | `CountryController` | 6 |
| `StateOrProvinceControllerTest.java` | `StateOrProvinceController` | 6 |
| `AddressServiceTest.java` | `AddressService` | 9 |
| `CountryServiceTest.java` | `CountryService` | 13 |
| `DistrictServiceTest.java` | `DistrictService` | 1 |
| `StateOrProvinceServiceTest.java` | `StateOrProvinceService` | 14 |

**Kết Quả Coverage:** Instructions **87%** | Branches **100%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho location](../assets/coverage/location-coverage.png)

### 2.4 Module `cart`

- **Branch:** `test/cart`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `CartItemServiceTest.java` | `CartItemService` | 10 |
| `ProductServiceTest.java` | `ProductService` | 1 |
| `CartItemControllerTest.java` | `CartItemController` | 12 |

**Kết Quả Coverage:** Instructions **88%** | Branches **68%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho cart](../assets/coverage/cart-coverage.png)

### 2.5 Module `tax`

- **Branch:** `test/tax`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `TaxClassServiceTest.java` | `TaxClassService` | 8 |
| `TaxRateServiceTest.java` | `TaxRateService` | 15 |
| `TaxClassControllerTest.java` | `TaxClassController` | 6 |
| `TaxRateControllerTest.java` | `TaxRateController` | 7 |

**Kết Quả Coverage:** Instructions **87%** | Branches **100%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho tax](../assets/coverage/tax-coverage.png)

### 2.6 Module `search`

- **Branch:** `test/search`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `ProductServiceTest.java` | `ProductService` | 4 |
| `ProductSyncDataServiceTest.java` | `ProductSyncDataService` | 7 |
| `ProductControllerTest.java` | `ProductController` | 2 |
| `ProductSyncDataConsumerTest.java` | `ProductSyncDataConsumer` | 3 |

**Kết Quả Coverage:** Instructions **85%** | Branches **61%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho search](../assets/coverage/search-coverage.png)

### 2.7 Module `webhook`

- **Branch:** `test/webhook`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| `WebhookControllerTest.java` | `WebhookController` | 6 |
| `WebhookServiceTest.java` | `WebhookService` | 11 |
| `EventServiceTest.java` | `EventService` | 1 |
| `OrderEventServiceTest.java` | `OrderEventService` | 4 |
| `ProductEventServiceTest.java` | `ProductEventService` | 2 |
| `WebhookMapperTest.java` | `WebhookMapper` | 7 |

**Kết Quả Coverage:** Instructions **78%** | Branches **65%**

**Hình Ảnh Minh Chứng:**

![Báo cáo JaCoCo coverage cho webhook](../assets/coverage/webhook-coverage.png)

### 2.8 Bảng Tổng Hợp Kết Quả Coverage (6 modules)

Yêu cầu tối thiểu: >= 70%

| Module | Coverage (Instructions) | Coverage (Branches) | Đạt >= 70% |
|--------|:-----------------------:|:-------------------:|:----------:|
| `customer` | 87% | 87% | ✅ |
| `location` | 87% | 100% | ✅ |
| `cart` | 88% | 68% | ✅ |
| `tax` | 87% | 100% | ✅ |
| `search` | 85% | 61% | ✅ |
| `webhook` | 78% | 65% | ✅ |

---

## 3. Tổng Hợp Coverage Toàn Dự Án (16 Modules)

| Service | Coverage (Instructions) | Coverage (Branches) | Đạt >= 70% |
|---------|:-----------------------:|:-------------------:|:----------:|
| media   | 80%                     | 65%                 | ✅          |
| product | 71%                     | 47%                 | ✅          |
| order   | 76%                     | 47%                 | ✅          |
| inventory| 89%                    | 70%                 | ✅          |
| payment | 72%                     | —                   | ✅          |
| promotion| 82%                    | —                   | ✅          |
| rating  | 85%                     | —                   | ✅          |
| delivery| 100%                    | N/A                 | ✅          |
| sampledata| 81%                   | N/A                 | ✅          |
| recommendation| 86%               | 48%                 | ✅          |
| customer| 87%                     | 87%                 | ✅          |
| location| 87%                     | 100%                | ✅          |
| cart    | 88%                     | 68%                 | ✅          |
| tax     | 87%                     | 100%                | ✅          |
| search  | 85%                     | 61%                 | ✅          |
| webhook | 78%                     | 65%                 | ✅          |

---

## 4. Hướng Dẫn Ghép Báo Cáo Cuối (TV4 Thực Hiện)

TV4 chịu trách nhiệm tổng hợp nội dung từ 4 thành viên thành một file `.docx` duy nhất để nộp.

**Quy trình:**

1. Thu thập nội dung từ TV1, TV2, TV3 (file `.md` đã điền đầy đủ ảnh và mô tả).
2. Dùng lệnh sau để gộp tất cả file `.md` thành một file duy nhất:

```bash
cd report/
cat main.md tv1-jenkins.md tv2-branch-protection.md tv3-security-scanning.md tv4-coverage-gate.md > bao-cao-tong-hop.md
```

3. Mở file gộp bằng trình đọc Markdown hoặc dùng Pandoc để chuyển sang Word:

```bash
pandoc bao-cao-tong-hop.md -o bao-cao-final.docx
```

4. Chỉnh sửa định dạng trong Word (trang bìa, font chữ, số trang).
5. Đổi tên file: `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` (MSSV sắp xếp tăng dần).
6. Cả nhóm review lần cuối trước khi nộp.

**Checklist trước khi nộp:**

| Mục | Trạng thái |
|-----|:----------:|
| Nội dung phần TV1 đã hoàn chỉnh với ảnh | |
| Nội dung phần TV2 đã hoàn chỉnh với ảnh | |
| Nội dung phần TV3 đã hoàn chỉnh với ảnh | |
| Nội dung phần TV4 đã hoàn chỉnh với ảnh | |
| Link GitHub repository có trong báo cáo | |
| Link Pull Request (Open) có trong báo cáo | |
| Tên file theo đúng định dạng MSSV | |

---

## 5. Vấn Đề Gặp Phải Và Cách Giải Quyết

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| `cart` và `tax` hiển thị coverage 100% trong JaCoCo nhưng số test case ít | Module nhỏ, ít lớp code nên dễ đạt coverage cao với ít test | Chấp nhận kết quả vì JaCoCo đo trên bytecode instruction, module đơn giản thì coverage tự nhiên cao |
| Một số service có Branch Coverage thấp (<70%) nhưng Instruction Coverage đạt | Các nhánh `if/else`, `switch-case` chưa được cover hết | Tập trung đảm bảo Instruction Coverage >= 70% theo yêu cầu đồ án, Branch Coverage là chỉ số tham khảo |
| Khó viết test cho các service có dependency phức tạp (Kafka, Elasticsearch) | Cần mock nhiều layer, ApplicationContext nặng | Sử dụng `@MockBean` và `@WebMvcTest` với `excludeAutoConfiguration` để giảm dependency |

---

*Phần này do TV4 thực hiện, viết báo cáo và chịu trách nhiệm tổng hợp file `.docx` cuối cùng.*
