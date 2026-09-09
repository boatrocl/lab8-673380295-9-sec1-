package com.example.lab8shop.strategy;

/**
 * Strategy Pattern จาก Lab 7 (ยังคงไว้ใน Lab 8 ตามข้อกำหนด)
 * Interface นี้คือ Abstraction ที่ทำให้ ProductService ไม่ผูกติดกับ Logic การคำนวณส่วนลดแบบใดแบบหนึ่ง (DIP)
 */
public interface DiscountStrategy {
    double applyDiscount(double originalPrice);
}
