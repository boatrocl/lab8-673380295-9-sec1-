package com.example.lab8shop.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: ส่วนลดช่วงเทศกาล 20%
 */
@Component("SEASONAL")
public class SeasonalSaleStrategy implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.20;

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * (1 - DISCOUNT_RATE);
    }
}
