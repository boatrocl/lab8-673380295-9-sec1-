package com.example.lab8shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity: Review (Part B - ความสัมพันธ์ 1:N กับ Product)
 * เป็นฝั่ง "Owning Side" ที่แท้จริงของความสัมพันธ์ เพราะมี FK (product_id) อยู่ในตารางนี้จริง ๆ
 * สินค้า 1 ชิ้น มีได้หลาย Review (1 -> N) แต่ Review 1 รายการ อ้างอิง Product ได้แค่ 1 ชิ้น (N -> 1)
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewer;

    @Column(nullable = false)
    private Integer rating; // ควรจำกัดค่า 1-5 ด้วย Validation เพิ่มเติมที่ Service/Controller

    @Column(length = 1000)
    private String comment;

    private LocalDate reviewDate;

    /**
     * @ManyToOne คือฝั่ง Owning Side ของความสัมพันธ์ 1:N เสมอ (ตรงข้ามกับความเข้าใจผิดที่คิดว่า "1" ฝั่งคือ Owning)
     * @JoinColumn(name = "product_id") จะสร้างคอลัมน์ FK ชื่อ product_id ในตาราง reviews
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
