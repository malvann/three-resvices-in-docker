package com.tsid.kitchen;

import com.tsid.kitchen.service.Kitchen;
import component.RestService;
import component.Sleeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RestService.class, Sleeper.class})
@Slf4j
public class KitchenApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(KitchenApp.class, args);
        context.getBean(Kitchen.class).startUp();

    }
}
