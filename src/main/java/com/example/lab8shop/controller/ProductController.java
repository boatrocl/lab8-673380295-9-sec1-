package com.example.lab8shop.controller;

import com.example.lab8shop.model.Product;
import com.example.lab8shop.model.ProductDetail;
import com.example.lab8shop.service.ProductService;
import com.example.lab8shop.model.Review;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller Layer (SRP: รับผิดชอบแค่ HTTP Request/Response กับการเลือก View ไม่มี Business Logic อยู่ในนี้เลย)
 * ใช้ Thymeleaf View: products/list.html, products/add.html, products/edit.html, products/delete.html
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // Constructor Injection ตามข้อกำหนดของ Lab (ห้ามใช้ @Autowired ที่ field)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /products : แสดงรายการสินค้าทั้งหมด พร้อม ProductDetail และจำนวน Review */
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    /** GET /products/add : แสดงฟอร์มเพิ่มสินค้าใหม่ */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        Product product = new Product();
        // เตรียม ProductDetail เปล่าไว้ล่วงหน้า เพื่อให้ Thymeleaf bind ฟอร์มแบบ nested object ได้ทันที
        product.setProductDetail(new ProductDetail());
        model.addAttribute("product", product);
        return "products/add";
    }

    /** POST /products/save : บันทึกสินค้าใหม่ (พร้อม ProductDetail และ Review ถ้ามีส่งมาด้วย) */
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    /** GET /products/edit/{id} : แสดงฟอร์มแก้ไขสินค้าเดิม */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product.getProductDetail() == null) {
            product.setProductDetail(new ProductDetail());
        }
        model.addAttribute("product", product);
        return "products/edit";
    }

    /** POST /products/update/{id} : บันทึกการแก้ไขสินค้า */
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute("product") Product product) {
        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    /** GET /products/delete/{id} : แสดงหน้ายืนยันการลบ (Confirm Delete) */
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "products/delete";
    }

    /** POST /products/delete/{id} : ดำเนินการลบสินค้าจริง (cascade ลบ ProductDetail และ Review) */
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    /** POST /products/{id}/reviews/add : เพิ่มรีวิวใหม่ (1:N) */
    @PostMapping("/{id}/reviews/add")
    public String addReview(@PathVariable Long id,
                            @RequestParam String reviewer,
                            @RequestParam Integer rating,
                            @RequestParam(required = false) String comment,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reviewDate) {
        Review review = Review.builder()
                .reviewer(reviewer)
                .rating(rating)
                .comment(comment)
                .reviewDate(reviewDate)
                .build();
        productService.addReviewToProduct(id, review);
        return "redirect:/products";
    }
}