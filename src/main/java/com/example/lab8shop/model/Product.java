package com.example.lab8shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity หลักของ Lab 8: Product
 * - ความสัมพันธ์ 1:1 กับ ProductDetail (Product เป็นฝั่ง Owning Side ถือ FK product_detail_id)
 * - ความสัมพันธ์ 1:N กับ Review (Product เป็นฝั่ง Inverse Side, mappedBy = "product")
 *
 * ชื่อ Class/Table ตั้งใจให้เรียบง่ายตาม Requirement ของ Lab
 * (นักศึกษาควรตั้งชื่อ "product" ตัวอย่างในฐานข้อมูลจริงให้มีรหัสนักศึกษา + Section ตาม Checklist ท้ายเอกสาร)
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String category;

    /**
     * เก็บประเภทส่วนลดเป็น String ("NONE", "MEMBER", "SEASONAL")
     * เพื่อให้ DiscountStrategyResolver (Strategy Pattern จาก Lab 7) เลือก Strategy ที่เหมาะสมตอน runtime
     * -> สอดคล้องกับหลัก OCP (Open/Closed Principle): เพิ่มส่วนลดแบบใหม่ได้โดยไม่ต้องแก้โค้ด Product/Service เดิม
     */
    @Column(name = "discount_type")
    private String discountType;

    /**
     * 1:1 Product <-> ProductDetail
     * Product เป็นฝั่ง "Owning Side" เพราะมี @JoinColumn (คอลัมน์ product_detail_id อยู่ในตาราง products)
     * cascade = ALL: บันทึก/ลบ Product แล้ว ProductDetail จะถูกจัดการตามไปด้วยอัตโนมัติ
     * orphanRemoval = true: ถ้าเอา ProductDetail ออกจาก Product แล้ว Hibernate จะลบแถวนั้นทิ้งจากฐานข้อมูล
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", referencedColumnName = "id")
    private ProductDetail productDetail;

    /**
     * 1:N Product -> Review
     * Product เป็นฝั่ง "Inverse Side" (mappedBy ชี้ไปที่ field "product" ใน Review.java)
     * Review เป็นฝั่ง "Owning Side" ที่แท้จริง เพราะมี FK product_id อยู่ในตาราง reviews
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * Helper method สำหรับ bidirectional relationship
     * ช่วยให้ทั้งสองฝั่ง (Product.reviews และ Review.product) sync กันเสมอ
     * ป้องกันบัค "FK เป็น null" ที่พบบ่อยเวลา save ความสัมพันธ์สองทิศทาง
     */
    public void addReview(Review review) {
        this.reviews.add(review);
        review.setProduct(this);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        review.setProduct(null);
    }
}
