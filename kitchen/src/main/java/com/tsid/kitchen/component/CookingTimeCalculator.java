package com.tsid.kitchen.component;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class CookingTimeCalculator {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public int getCookingMinutes(String dishName) {
        return RANDOM.nextInt(25);
    }
}
