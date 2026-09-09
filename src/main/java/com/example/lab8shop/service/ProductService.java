package com.example.lab8shop.service;

import com.example.lab8shop.model.Product;
import com.example.lab8shop.model.Review;

import java.util.List;

/**
 * Service Layer Abstraction
 * Controller จะพึ่งพา Interface นี้ ไม่ใช่ ProductServiceImpl โดยตรง (DIP)
 * ทำให้สามารถสลับ Implementation หรือเขียน Unit Test ด้วย Mock ได้ง่าย
 */
public interface ProductService {

    Product addReviewToProduct(Long productId, Review review);
    
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product saveProduct(Product product);

    Product updateProduct(Long id, Product updatedProduct);

    void deleteProduct(Long id);

    double calculateFinalPrice(Product product);
}


