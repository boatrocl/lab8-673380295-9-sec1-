package com.example.lab8shop.repository;

import com.example.lab8shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Layer: จัดการ CRUD ของ Product
 * Spring Data JPA จะ implement method พื้นฐาน (findAll, findById, save, deleteById ฯลฯ) ให้อัตโนมัติ
 * เนื่องจากตั้ง cascade = ALL ไว้ที่ Entity แล้ว การ save(Product) จะพ่วง ProductDetail และ Review ไปด้วย
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
