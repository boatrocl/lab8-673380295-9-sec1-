package com.example.lab8shop.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ตัวช่วยเลือก Strategy ที่ถูกต้องตอน runtime (Factory-like Resolver)
 *
 * Spring จะ inject Bean ทุกตัวที่ implements DiscountStrategy เข้ามาเป็น Map<String, DiscountStrategy>
 * โดยใช้ชื่อ Bean (จาก @Component("NONE"), @Component("MEMBER"), @Component("SEASONAL")) เป็น Key
 * -> ทำให้ Resolver "ไม่ต้องรู้จัก" Class ของ Strategy แต่ละตัวเลย (DIP + OCP)
 *    ถ้าจะเพิ่มส่วนลดแบบใหม่ในอนาคต แค่สร้าง Class ใหม่ implements DiscountStrategy แล้วใส่ @Component("XXX")
 *    โดยไม่ต้องแก้ไขไฟล์นี้เลย
 */
@Component
public class DiscountStrategyResolver {

    private final Map<String, DiscountStrategy> strategies;

    public DiscountStrategyResolver(Map<String, DiscountStrategy> strategies) {
        this.strategies = strategies;
    }

    public DiscountStrategy resolve(String discountType) {
        return strategies.getOrDefault(discountType, strategies.get("NONE"));
    }
}
