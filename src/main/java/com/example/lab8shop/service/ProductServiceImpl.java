package com.example.lab8shop.service;

import com.example.lab8shop.model.Product;
import com.example.lab8shop.model.ProductDetail;
import com.example.lab8shop.model.Review;
import com.example.lab8shop.repository.ProductRepository;
import com.example.lab8shop.strategy.DiscountStrategy;
import com.example.lab8shop.strategy.DiscountStrategyResolver;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Layer Implementation
 * รับผิดชอบ Business Logic ทั้งหมดของ Product รวมถึงการ sync ความสัมพันธ์กับ ProductDetail และ Review
 * (SRP: Controller ไม่ยุ่งกับ Logic นี้เลย มีหน้าที่แค่รับ Request/ส่ง Response)
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final DiscountStrategyResolver discountStrategyResolver;

    /**
     * Constructor Injection ตามข้อกำหนดของ Lab (ห้ามใช้ @Autowired ที่ field)
     * Service พึ่งพา Abstraction สองตัว (ProductRepository interface, DiscountStrategyResolver)
     * ไม่ได้ผูกติดกับ Implementation ใด ๆ โดยตรง -> สอดคล้องกับ DIP
     */
    public ProductServiceImpl(ProductRepository productRepository,
                               DiscountStrategyResolver discountStrategyResolver) {
        this.productRepository = productRepository;
        this.discountStrategyResolver = discountStrategyResolver;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบสินค้ารหัส: " + id));
    }

    @Override
    public Product saveProduct(Product product) {
        // เพราะตั้ง cascade = ALL ไว้ที่ Product ทั้งสองความสัมพันธ์ (ProductDetail และ Review)
        // การเรียก save(product) เพียงครั้งเดียว Hibernate จะ INSERT ทั้ง 3 ตารางให้อัตโนมัติ
        // แต่ฝั่ง Review (Owning Side) ต้องถูก set FK (product) ก่อนเสมอ ไม่งั้น product_id จะเป็น null
        linkReviewsToProduct(product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setDiscountType(updatedProduct.getDiscountType());

        // อัปเดต ProductDetail (1:1): แก้ค่าใน object เดิมแทนการสร้างแถวใหม่ เพื่อคง id/FK เดิมไว้
        ProductDetail incomingDetail = updatedProduct.getProductDetail();
        if (incomingDetail != null) {
            ProductDetail currentDetail = existing.getProductDetail();
            if (currentDetail == null) {
                existing.setProductDetail(incomingDetail);
            } else {
                currentDetail.setDescription(incomingDetail.getDescription());
                currentDetail.setWarranty(incomingDetail.getWarranty());
                currentDetail.setWeight(incomingDetail.getWeight());
                currentDetail.setDimensions(incomingDetail.getDimensions());
                currentDetail.setManufacturedCountry(incomingDetail.getManufacturedCountry());
            }
        }

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        // เพราะตั้ง orphanRemoval = true และ cascade = ALL ไว้แล้ว
        // การลบ Product จะลบ ProductDetail และ Review ที่ผูกอยู่ทั้งหมดให้อัตโนมัติ (ไม่ต้องลบเองทีละตาราง)
        productRepository.deleteById(id);
    }

    @Override
    public double calculateFinalPrice(Product product) {
        // Strategy Pattern: เลือกวิธีคำนวณส่วนลดตาม discountType โดย Service ไม่ต้องมี if-else ยาว ๆ (OCP)
        DiscountStrategy strategy = discountStrategyResolver.resolve(product.getDiscountType());
        return strategy.applyDiscount(product.getPrice());
    }

    /**
     * Bidirectional relationship ต้อง sync สองฝั่งเสมอ:
     * ต่อให้ Product.reviews มีข้อมูลอยู่ แต่ถ้า Review.product เป็น null
     * Hibernate จะ insert product_id เป็น NULL ลงตาราง reviews -> ทำให้ FK ผิด
     */
    private void linkReviewsToProduct(Product product) {
        List<Review> reviews = product.getReviews();
        if (reviews != null) {
            reviews.forEach(review -> review.setProduct(product));
        }
    }

    @Override
    public Product addReviewToProduct(Long productId, Review review) {
        Product product = getProductById(productId);
        product.addReview(review);
        return productRepository.save(product);
    }
}
