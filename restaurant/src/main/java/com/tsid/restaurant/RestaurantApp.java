package com.tsid.restaurant;

import com.tsid.restaurant.service.Restaurant;
import com.tsid.restaurant.configurations.PortValues;
import component.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RestService.class, PortValues.class})
@Slf4j
public class RestaurantApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RestaurantApp.class, args);
        context.getBean(Restaurant.class).startAsync();
    }
}
