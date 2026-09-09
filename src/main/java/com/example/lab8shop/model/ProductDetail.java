package com.example.lab8shop.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity: ProductDetail (Part A - ความสัมพันธ์ 1:1 กับ Product)
 * เป็นฝั่ง "Inverse Side" ของความสัมพันธ์ (mappedBy = "productDetail")
 * ไม่มี FK อยู่ในตารางนี้ — FK (product_detail_id) อยู่ในตาราง products แทน
 */
@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String description;

    private String warranty;

    private Double weight;

    private String dimensions;

    private String manufacturedCountry;

    /**
     * mappedBy = "productDetail" หมายถึง ความสัมพันธ์นี้ถูก "ควบคุม" โดย field productDetail ใน Product.java
     * ProductDetail จึงไม่ต้องมี @JoinColumn ของตัวเอง
     */
    @OneToOne(mappedBy = "productDetail")
    private Product product;
}
