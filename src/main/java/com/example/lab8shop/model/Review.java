package com.example.lab8shop.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewer;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    private LocalDate reviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Constructors
    public Review() {}

    public Review(Long id, String reviewer, Integer rating, String comment, LocalDate reviewDate, Product product) {
        this.id = id;
        this.reviewer = reviewer;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.product = product;
    }

    // Manual Builder (แทนที่ @Builder ของ Lombok เพื่อให้ Compile ผ่านแน่นอน)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String reviewer;
        private Integer rating;
        private String comment;
        private LocalDate reviewDate;
        private Product product;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder reviewer(String reviewer) { this.reviewer = reviewer; return this; }
        public Builder rating(Integer rating) { this.rating = rating; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }
        public Builder reviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; return this; }
        public Builder product(Product product) { this.product = product; return this; }

        public Review build() {
            return new Review(id, reviewer, rating, comment, reviewDate, product);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}