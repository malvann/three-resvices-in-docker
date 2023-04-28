package com.tsid.kitchen.controller;

import com.tsid.kitchen.controller.requestwraper.DishCreateRequest;
import com.tsid.kitchen.controller.requestwraper.DishUpdateRequest;
import com.tsid.kitchen.model.Dish;
import com.tsid.kitchen.model.KitchenOrder;
import com.tsid.kitchen.repository.DishRepository;
import com.tsid.kitchen.service.Kitchen;
import exception.DataFormatException;
import exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static constant.UrlPath.KitchenUrl.*;

@RestController
@AllArgsConstructor
@Slf4j
public class KitchenController {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final Kitchen kitchen;
    private final DishRepository dishRepository;

    @PostMapping(RECEIVE_ORDERS_URL)
    public ResponseEntity<List<Long>> receiveRestaurantOrders(@RequestBody List<KitchenOrder> request) {
        kitchen.receiveRestaurantOrders(request);
        List<Long> receivedOrders = request.stream().map(KitchenOrder::getOrderId).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(receivedOrders);
    }

    @GetMapping(DAY_DISH_SET)
    public ResponseEntity<Set<String>> getDayDishSet() {
        Set<String> dailyDishList = kitchen.getDailyDishNameSet();
        log.info("Daily dishes {} are sent to Restaurant.", dailyDishList);
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyDishList);
    }

    @GetMapping(DOM_URL)
    public ResponseEntity<List<Dish>> findAllDishes() {
        return ResponseEntity.ok(dishRepository.findAll());
    }

    @GetMapping(DOM_URL + "/{id}")
    public ResponseEntity<Dish> findDishById(@PathVariable Long id) {
        return ResponseEntity.ok(dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString())));
    }

    @GetMapping(DOM_URL + "/{name}")
    public ResponseEntity<Dish> findDishByName(@PathVariable String name) {
        return ResponseEntity.ok(dishRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(name)));
    }

    @PostMapping(DOM_URL + "/create")
    public ResponseEntity<Dish> createDish(@RequestBody DishCreateRequest request) {
        Dish dish = fillByRequest(new Dish(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dishRepository.save(dish));
    }

    @PutMapping(DOM_URL + "/update")
    public ResponseEntity<Dish> updateDish(@RequestBody DishUpdateRequest request) {
        Dish dish = dishRepository.findById(request.getId())
                .orElseThrow(() -> new DataFormatException("No such element"));
        fillByRequest(dish, request);
        return ResponseEntity.ok(dishRepository.save(dish));
    }

    @GetMapping(DOM_URL + "/delete/{id}")
    public ResponseEntity<Long> deleteDish(@PathVariable Long id) {
        dishRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    private static Dish fillByRequest(Dish dish, DishCreateRequest request) {
        if (dish.getId() == null) {
            dish.setId(RANDOM.nextLong());
            dish.setCreated(new Timestamp(new Date().getTime()));
        } else {
            dish.setUpdated(new Timestamp(new Date().getTime()));
        }
        dish.setName(request.getName());
        dish.setRecipe(request.getRecipe());
        return dish;
    }
}
