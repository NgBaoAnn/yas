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

```
[HÌNH: Đoạn cấu hình JaCoCo plugin với execution "check" và minimum 0.70]
```

**Hình 1.2 — Pipeline thất bại khi coverage dưới ngưỡng 70% (trường hợp demo)**

```
[HÌNH: Jenkins build FAIL với thông báo "Coverage below minimum: Instructions: X% < 70%"]
```

**Hình 1.3 — Pipeline thành công sau khi bổ sung đủ unit test**

```
[HÌNH: Jenkins build SUCCESS — tất cả stage màu xanh]
```

**Hình 1.4 — Báo cáo JaCoCo Coverage hiển thị trong giao diện Jenkins**

```
[HÌNH: Jenkins > Build > JaCoCo Coverage Report với biểu đồ coverage]
```

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
| [Tên file] | [Tên lớp] | |

**Kết Quả Coverage:** Instructions % | Branches %

**Hình Ảnh Minh Chứng:**
```
[HÌNH: Terminal output BUILD SUCCESS cho customer]
[HÌNH: Báo cáo JaCoCo coverage cho customer]
```

### 2.3 Module `location`

- **Branch:** `test/location`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| [Tên file] | [Tên lớp] | |

**Kết Quả Coverage:** Instructions % | Branches %

**Hình Ảnh Minh Chứng:**
```
[HÌNH: Terminal output BUILD SUCCESS cho location]
[HÌNH: Báo cáo JaCoCo coverage cho location]
```

### 2.4 Module `cart`

- **Branch:** `test/cart`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| [Tên file] | [Tên lớp] | |

**Kết Quả Coverage:** Instructions % | Branches %

**Hình Ảnh Minh Chứng:**
```
[HÌNH: Terminal output BUILD SUCCESS cho cart]
[HÌNH: Báo cáo JaCoCo coverage cho cart]
```

### 2.5 Module `tax`

- **Branch:** `test/tax`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| [Tên file] | [Tên lớp] | |

**Kết Quả Coverage:** Instructions % | Branches %

**Hình Ảnh Minh Chứng:**
```
[HÌNH: Terminal output BUILD SUCCESS cho tax]
[HÌNH: Báo cáo JaCoCo coverage cho tax]
```

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
```
[HÌNH: Terminal output "Tests run: 16, Failures: 0, Errors: 0, Skipped: 2 — BUILD SUCCESS"]
```
![Báo cáo JaCoCo coverage cho search](../screenshots/test/search-coverage-report.png)

### 2.7 Module `webhook`

- **Branch:** `test/webhook`
- **Pull Request:** `[Link PR]`

**Danh Sách File Test:**
| File Test | Lớp được kiểm thử | Số test case |
|-----------|-------------------|:------------:|
| [Tên file] | [Tên lớp] | |

**Kết Quả Coverage:** Instructions % | Branches %

**Hình Ảnh Minh Chứng:**
```
[HÌNH: Terminal output BUILD SUCCESS cho webhook]
[HÌNH: Báo cáo JaCoCo coverage cho webhook]
```

### 2.8 Bảng Tổng Hợp Kết Quả Coverage (6 modules)

Yêu cầu tối thiểu: >= 70%

| Module | Coverage (Instructions) | Coverage (Branches) | Đạt >= 70% |
|--------|:-----------------------:|:-------------------:|:----------:|
| `customer` | % | % | |
| `location` | % | % | |
| `cart` | % | % | |
| `tax` | % | % | |
| `search` | 85% | 61% | Đạt |
| `webhook` | % | % | |

---

## 3. Tổng Hợp Coverage Toàn Dự Án (16 Modules)

| Service | Coverage (Instructions) | Coverage (Branches) | Đạt >= 70% |
|---------|:-----------------------:|:-------------------:|:----------:|
| media   | %                       | %                   |            |
| product | %                       | %                   |            |
| order   | %                       | %                   |            |
| inventory| %                       | %                   |            |
| payment | %                       | %                   |            |
| promotion| %                       | %                   |            |
| rating  | %                       | %                   |            |
| delivery| %                       | %                   |            |
| sampledata| %                      | %                   |            |
| recommendation| %                  | %                   |            |
| customer| %                       | %                   |            |
| location| %                       | %                   |            |
| cart    | %                       | %                   |            |
| tax     | %                       | %                   |            |
| search  | 85%                     | 61%                 | Đạt        |
| webhook | %                       | %                   |            |

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
| [Điền vào] | | |

---

*Phần này do TV4 thực hiện, viết báo cáo và chịu trách nhiệm tổng hợp file `.docx` cuối cùng.*
