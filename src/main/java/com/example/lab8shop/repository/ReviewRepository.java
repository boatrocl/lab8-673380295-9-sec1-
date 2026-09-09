package com.example.lab8shop.repository;

import com.example.lab8shop.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository Layer: จัดการ CRUD ของ Review โดยตรง (ถ้าต้องการทำหน้าจัดการ Review แยกในอนาคต)
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);
}
