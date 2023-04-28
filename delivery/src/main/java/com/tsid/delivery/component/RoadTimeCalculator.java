package com.tsid.delivery.component;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RoadTimeCalculator {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public int getWaitingMinutes(String address) {
        return RANDOM.nextInt(120);
    }
}
