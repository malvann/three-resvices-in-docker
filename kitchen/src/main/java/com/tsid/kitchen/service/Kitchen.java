package com.tsid.kitchen.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.tsid.kitchen.component.CookingTimeCalculator;
import com.tsid.kitchen.configurations.PortValues;
import com.tsid.kitchen.model.Dish;
import com.tsid.kitchen.model.KitchenOrder;
import com.tsid.kitchen.repository.DishRepository;
import component.RestService;
import component.Sleeper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static constant.UrlPath.RestaurantUrl.GET_DONE_KITCHEN_URL;

@Service
@AllArgsConstructor
@Slf4j
public class Kitchen extends AbstractScheduledService {
    private final PortValues portValues;
    private final DishRepository dishRepository;
    private final CookingTimeCalculator cookingTimeCalculator;
    private final RestService restService;
    private final Sleeper sleeper;

    private List<Dish> dailyDishList;
    private Set<String> dailyDishNameSet;
    private final Queue<KitchenOrder> orderQueue = new PriorityQueue<>();

    //при стерте или в 9:00 достать все available и положить в dishDaySet
    private void updateDishDaySet() {
        dailyDishList = dishRepository.findAllByAvailableTrue();
        dailyDishNameSet = dailyDishList.stream().map(Dish::getName).collect(Collectors.toSet());
        log.info("Daily dishes are updated: {}", dailyDishNameSet);
    }

    //содержится ли список блюд в dishDaySet
    public List<String> checkDishes(List<String> dishes) {
        List<String> checkedDishes = dishes.stream().filter(o -> dailyDishNameSet.contains(o)).toList();
        log.info("Available dishes: {}", checkedDishes);
        return checkedDishes;
    }

    //принять от Restaurant заказы, положить в orders
    public void receiveRestaurantOrders(List<KitchenOrder> request) {
        this.orderQueue.addAll(request);
    }

    //выполнять заказы по очереди если она не пустая и > 5, отправляет ответ с готовыми заказами
    private Optional<KitchenOrder> processOrder() {
        if (orderQueue.peek() == null || orderQueue.size() < 5) return Optional.empty();
        KitchenOrder order;
        synchronized (orderQueue) {
            order = orderQueue.poll();
        }
        assert order != null;
        log.info("Order in process: {}", order.getOrderId());
        int minutesNeedToCook = order.getDishList().stream().map(cookingTimeCalculator::getCookingMinutes)
                .mapToInt(x -> x).sum();
        sleeper.sleep(minutesNeedToCook);
        log.info("Order {} is done.", order.getOrderId());
        return Optional.of(order);
    }

    @Override
    public void startUp() {
        updateDishDaySet();
    }

    @Override
    protected @NonNull Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 10, TimeUnit.SECONDS);
    }

    protected void runOneIteration() {
        Optional<KitchenOrder> order = processOrder();
        order.ifPresent(kReq -> restService.doPostRequest(portValues.getRestaurantPort() + GET_DONE_KITCHEN_URL, kReq.getOrderId(), Long.class));
    }

    public Set<String> getDailyDishNameSet() {
        return dailyDishNameSet;
    }

    // todo если очередь больше чем ? отослать сообщение, что сервес перегружен и нужно ждать дольше
}
