package com.tsid.restaurant.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PortValues {
    @Value("${kitchen.port}")
    private String kitchenPort;
    @Value("${delivery.port}")
    private String deliveryPort;
}
