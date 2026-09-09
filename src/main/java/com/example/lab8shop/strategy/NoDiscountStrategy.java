package com.example.lab8shop.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: ไม่มีส่วนลด
 * @Component("NONE") -> ตั้งชื่อ Bean ให้ตรงกับค่า discountType ที่เก็บใน Product เพื่อให้ Resolver หาเจอ
 */
@Component("NONE")
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice;
    }
}
