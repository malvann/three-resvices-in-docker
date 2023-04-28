package com.tsid.kitchen.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PortValues {
    @Value("${restaurant.port}")
    private String restaurantPort;
}
