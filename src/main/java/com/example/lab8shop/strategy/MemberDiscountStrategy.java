package com.example.lab8shop.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: ส่วนลดสมาชิก 10%
 */
@Component("MEMBER")
public class MemberDiscountStrategy implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.10;

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * (1 - DISCOUNT_RATE);
    }
}
