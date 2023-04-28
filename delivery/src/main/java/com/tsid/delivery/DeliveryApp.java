package com.tsid.delivery;

import com.tsid.delivery.service.DeliveryClub;
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
public class DeliveryApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DeliveryApp.class, args);
        context.getBean(DeliveryClub.class).startUp();
    }
}