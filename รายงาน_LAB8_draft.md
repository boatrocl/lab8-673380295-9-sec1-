# รายงาน LAB 8: Table Relationships — Product Shop
**วิชา CP353002 Principles of Software Design**
*(แทนที่ส่วนนี้ด้วย ชื่อ-รหัสนักศึกษา-Section ของคุณ)*

---

## ส่วนที่ 1: หลักการออกแบบ

### 1.1 การประยุกต์ใช้ SOLID Principles

**SRP (Single Responsibility Principle)**
แต่ละ Layer มีหน้าที่เดียวชัดเจน: `ProductController` รับผิดชอบแค่การรับ HTTP Request และเลือก View/Redirect เท่านั้น ไม่มี Business Logic ปนอยู่; `ProductServiceImpl` รับผิดชอบ Business Logic ทั้งหมด เช่น การ sync ความสัมพันธ์ระหว่าง Product, ProductDetail และ Review รวมถึงการคำนวณราคาหลังหักส่วนลด; `ProductRepository` รับผิดชอบแค่การเข้าถึงข้อมูลในฐานข้อมูลเท่านั้น

**OCP (Open/Closed Principle)**
เห็นได้ชัดในส่วนของ Strategy Pattern สำหรับคำนวณส่วนลด: `DiscountStrategyResolver` ไม่มีการเขียน `if-else` หรือ `switch` เพื่อเช็คประเภทส่วนลดเลย แต่ใช้การ inject `Map<String, DiscountStrategy>` ที่ Spring รวบรวม Bean ทั้งหมดที่ implement `DiscountStrategy` มาให้อัตโนมัติ หากในอนาคตต้องการเพิ่มส่วนลดแบบใหม่ เช่น "FlashSale 30%" เพียงสร้าง Class ใหม่ implement `DiscountStrategy` และติด `@Component("FLASH")` โดยไม่ต้องแก้โค้ดเดิมแม้แต่บรรทัดเดียว — ระบบ "เปิดให้ขยาย แต่ปิดการแก้ไข"

**DIP (Dependency Inversion Principle)**
`ProductController` และ `ProductServiceImpl` ไม่ได้พึ่งพา Class ที่เป็น Implementation โดยตรง แต่พึ่งพา Interface (`ProductService`, `ProductRepository`) ผ่าน Constructor Injection ทำให้ Module ระดับสูง (Controller/Service) ไม่ถูกผูกติดกับรายละเอียดการทำงานของ Module ระดับล่าง (Repository/Strategy Implementation) และสามารถสลับ Implementation หรือใส่ Mock เพื่อทดสอบได้ง่าย

### 1.2 ความแตกต่างระหว่าง 1:1 และ 1:N พร้อมเหตุผลในการออกแบบ

| ประเด็น | 1:1 (Product ↔ ProductDetail) | 1:N (Product → Review) |
|---|---|---|
| ความหมาย | สินค้า 1 ชิ้น มีรายละเอียดเชิงลึกได้เพียง 1 ชุด | สินค้า 1 ชิ้น มีรีวิวได้หลายรายการ |
| ฝั่งที่ถือ FK (Owning Side) | `Product` (มีคอลัมน์ `product_detail_id`) | `Review` (มีคอลัมน์ `product_id`) |
| Annotation ฝั่ง Owning | `@OneToOne` + `@JoinColumn` | `@ManyToOne` + `@JoinColumn` |
| Annotation ฝั่ง Inverse | `@OneToOne(mappedBy = "productDetail")` | `@OneToMany(mappedBy = "product")` |
| เหตุผลที่ออกแบบเช่นนี้ | แยก ProductDetail ออกจาก Product เพื่อไม่ให้ตาราง `products` มีคอลัมน์เยอะเกินไป (ข้อมูลที่ไม่ได้ใช้บ่อย เช่น dimensions, manufacturedCountry ถูกแยกเก็บต่างหาก) และง่ายต่อการขยายในอนาคตหากต้องการให้สินค้าบางประเภทไม่มี Detail ก็ยังทำได้ (FK เป็น nullable) | เพราะรีวิวมีจำนวนไม่แน่นอนและเพิ่มขึ้นเรื่อย ๆ ตามเวลา การเก็บเป็นตารางแยกที่อ้างอิงกลับมาที่ Product ด้วย FK เดียว จึงเหมาะกับข้อมูลเชิงประวัติศาสตร์ (transactional/log-like data) มากกว่าการฝังไว้ในตาราง products โดยตรง |

จุดที่ต้องระวังคือใน 1:N ฝั่งที่ถือ FK เสมอคือฝั่ง "Many" (`Review`) ไม่ใช่ฝั่ง "One" (`Product`) ซึ่งเป็นความเข้าใจผิดที่พบบ่อยของนักศึกษาที่เพิ่งเริ่มเรียน JPA

### 1.3 Execution Flow: HTTP Request → Controller → Service → Repository → Database

ตัวอย่าง Flow ของการบันทึกสินค้าใหม่ผ่าน `POST /products/save`:

1. **HTTP Request**: ผู้ใช้กรอกฟอร์มเพิ่มสินค้า (รวมถึงข้อมูล ProductDetail) แล้วกด Submit ทำให้ Browser ส่ง `POST /products/save` พร้อม Form Data
2. **Controller**: `ProductController.saveProduct()` รับ Request ผ่าน `@ModelAttribute` ซึ่ง Spring MVC จะ bind ข้อมูลจากฟอร์มเป็น Object `Product` (รวมถึง Nested Object `ProductDetail`) ให้อัตโนมัติ จากนั้นส่งต่อให้ Service ทันทีโดยไม่มี Logic แทรก
3. **Service**: `ProductServiceImpl.saveProduct()` ทำหน้าที่ sync ความสัมพันธ์ (เช่น set FK ของ Review แต่ละรายการให้ชี้กลับมาที่ Product) ก่อนเรียก Repository
4. **Repository**: `ProductRepository.save()` (มาจาก `JpaRepository`) ส่งคำสั่งผ่าน Hibernate ซึ่งแปลง Entity Graph เป็น SQL `INSERT` ให้อัตโนมัติตาม Cascade ที่กำหนดไว้ (บันทึกทั้ง 3 ตารางในธุรกรรมเดียว)
5. **Database**: PostgreSQL บันทึกข้อมูลลงตาราง `products`, `product_details`, และ `reviews` พร้อมสร้างความสัมพันธ์ผ่าน Foreign Key
6. **Response**: Controller redirect กลับไปที่ `GET /products` เพื่อแสดงรายการสินค้าที่อัปเดตแล้ว

---

## ส่วนที่ 2: คำอธิบายโค้ด

### 2.1 Entity Layer
- **Product.java**: เป็นศูนย์กลางของทั้งสองความสัมพันธ์ ถือ FK ของ ProductDetail (`@OneToOne` + `@JoinColumn`) และเป็นฝั่ง Inverse ของ Review (`@OneToMany(mappedBy = "product")`) มี Helper Method `addReview()`/`removeReview()` เพื่อจัดการ Bidirectional Relationship ให้ทั้งสองฝั่ง sync กันเสมอ ป้องกันปัญหา FK เป็น null ที่เกิดจากการ set ความสัมพันธ์แค่ฝั่งเดียว
- **ProductDetail.java**: เป็นฝั่ง Inverse ของ 1:1 ไม่มี FK เป็นของตัวเอง ใช้ `mappedBy = "productDetail"` อ้างอิงกลับไปยัง field ใน Product
- **Review.java**: เป็นฝั่ง Owning ที่แท้จริงของ 1:N มี field `product` ที่ผูกกับ `@ManyToOne` และ `@JoinColumn(name = "product_id")` ซึ่งเป็นคอลัมน์ FK จริงในฐานข้อมูล

### 2.2 Service Layer และ Constructor Injection
`ProductServiceImpl` รับ `ProductRepository` และ `DiscountStrategyResolver` ผ่าน Constructor (ไม่ใช้ `@Autowired` ที่ field) ดังนี้:

```java
public ProductServiceImpl(ProductRepository productRepository,
                           DiscountStrategyResolver discountStrategyResolver) {
    this.productRepository = productRepository;
    this.discountStrategyResolver = discountStrategyResolver;
}
```

ข้อดีของการทำแบบนี้คือ:
1. Dependency ทุกตัวเป็น `final` การันตีว่าจะไม่ถูกเปลี่ยนแปลงหลังสร้าง Object แล้ว (Immutability)
2. เขียน Unit Test ได้ง่ายขึ้นมาก เพราะสามารถส่ง Mock Object เข้าไปทาง Constructor ได้โดยตรง โดยไม่ต้องพึ่ง Spring Container
3. เห็น Dependency ทั้งหมดของ Class ได้จาก Constructor เดียว ทำให้โค้ดอ่านง่ายและตรวจสอบ Coupling ได้ทันที

### 2.3 Controller Layer และ Constructor Injection
เช่นเดียวกัน `ProductController` รับ `ProductService` ผ่าน Constructor:

```java
public ProductController(ProductService productService) {
    this.productService = productService;
}
```

Controller ไม่รู้จักและไม่ควรรู้จัก `ProductServiceImpl` เลย รู้จักแค่ Interface `ProductService` เท่านั้น ตาม DIP ทำให้ในอนาคตหากต้องเปลี่ยน Implementation ของ Service (เช่น เพิ่ม Caching Layer) จะไม่กระทบ Controller แม้แต่น้อย

---

## Checklist ก่อนส่งงาน

- [ ] ตั้งชื่อสินค้าตัวอย่างในฐานข้อมูลให้มี **รหัสนักศึกษา + Section** ตามที่อาจารย์กำหนด (เช่น `"64010000_Sec1_เสื้อยืด"`) เพื่อให้ตรวจสอบตัวตนได้
- [ ] เปิด pgAdmin เข้าไปเช็คว่าตาราง `products`, `product_details`, `reviews` ถูกสร้างขึ้นจริงหลังรัน Hibernate ด้วย `ddl-auto=update`
- [ ] ตรวจสอบว่าตาราง `products` มีคอลัมน์ `product_detail_id` และมี Foreign Key Constraint ชี้ไปที่ `product_details.id`
- [ ] ตรวจสอบว่าตาราง `reviews` มีคอลัมน์ `product_id` และมี Foreign Key Constraint ชี้ไปที่ `products.id`
- [ ] ทดสอบเพิ่ม/แก้ไข/ลบสินค้าอย่างน้อย 1 รอบ แล้วดูว่าข้อมูลใน 3 ตาราง sync กันถูกต้อง (โดยเฉพาะตอนลบ Product ต้องเช็คว่า ProductDetail และ Review ที่เกี่ยวข้องหายไปด้วย เพราะตั้ง cascade ไว้)
- [ ] ตรวจสอบว่าไม่มีการใช้ `@Autowired` บน field ที่ใดเลยในโปรเจกต์ (ใช้ Constructor Injection ทั้งหมด)
- [ ] ทดสอบ Strategy Pattern ครบทั้ง 3 แบบ (NONE, MEMBER, SEASONAL) ว่าคำนวณราคาหลังหักส่วนลดถูกต้อง
- [ ] เช็ค Log SQL (`spring.jpa.show-sql=true`) ว่า Hibernate สร้างคำสั่ง INSERT/UPDATE ตามที่คาดไว้ ไม่มี Query ที่ผิดปกติหรือ N+1 Problem ที่ชัดเจนเกินไป
- [ ] ตรวจการสะกดชื่อ field ในฟอร์ม (Thymeleaf `th:field`) ให้ตรงกับชื่อ field ใน Entity ทุกตัวอักษร มิเช่นนั้นข้อมูล Nested Object (เช่น ProductDetail) จะไม่ถูก bind
- [ ] แนบภาพหน้าจอ pgAdmin ที่แสดง Table Structure และ Foreign Key ทั้งสองความสัมพันธ์ลงในรายงาน PDF ก่อนส่ง
