package com.tsid.delivery.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.tsid.delivery.component.RoadTimeCalculator;
import com.tsid.delivery.configurations.PortValues;
import com.tsid.delivery.model.Deliver;
import com.tsid.delivery.model.DeliveryOrder;
import com.tsid.delivery.repository.DeliverRepository;
import component.RestService;
import component.Sleeper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static constant.UrlPath.RestaurantUrl.GET_DONE_DELIVERY_URL;

@Service
@AllArgsConstructor
@Slf4j
public class DeliveryClub extends AbstractScheduledService {
    private static final int MAX_ORDER_SIZE = 7;
    private final PortValues portValues;
    private final DeliverRepository deliverRepository;
    private final RoadTimeCalculator roadTimeCalculator;
    private final RestService restService;
    private final Sleeper sleeper;

    private final Queue<Deliver> freeDelivers = new LinkedList<>();
    private final Queue<DeliveryOrder> orderQueue = new PriorityQueue<>();

    //в 9:00 достать все !busy и положить в freeDelivers
    public synchronized void updateFreeDelivers() {
        freeDelivers.addAll(deliverRepository.findAllByBusyFalse());
        log.info("Available delivers deliverers: {}", freeDelivers.stream()
                .map(deliver -> "%s:%s".formatted(deliver.getName(), deliver.getContact()))
                .toList());
    }

    // принимать сообшение от Restaurant, класть в orders
    public synchronized void receiveRestaurantOrders(List<DeliveryOrder> request) {
        this.orderQueue.addAll(new PriorityQueue<>(request));
    }

    private Deliver getFreeDeliver() {
        Deliver deliver = freeDelivers.poll();
        assert deliver != null;
        deliver.setBusy(true);
        deliverRepository.save(deliver);
        log.info("Deliver {} starts work.", deliver);
        return deliver;
    }

    private void returnDeliverToQueue(Deliver deliver) {
        deliver.setBusy(false);
        deliverRepository.save(deliver);
        freeDelivers.add(deliver);
    }

    // выполнять - спать сколько скажет RoadTimeCalculator, отправляет ответ с готовыми заказами
    private Optional<List<DeliveryOrder>> processOrderByDeliver(Deliver deliver) {
        List<DeliveryOrder> orderList = new ArrayList<>();
        synchronized (orderQueue) {
            for (int i = 0; i < MAX_ORDER_SIZE; i++) {
                orderList.add(orderQueue.poll());
            }
        }
        log.info("Orders {} in process by deliver {}", orderList, deliver.getName());
        int minutesNeedToDeliver = orderList.stream().map(o -> roadTimeCalculator.getWaitingMinutes(o.getAddress()))
                .mapToInt(x -> x).sum();
        sleeper.sleep(minutesNeedToDeliver);
        log.info("Orders {} is done.", orderList);
        returnDeliverToQueue(deliver);
        return Optional.of(orderList);
    }

    @Override
    public void startUp() {
        updateFreeDelivers();
    }

    @Override
    protected @NonNull Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 10, TimeUnit.SECONDS);
    }

    protected void runOneIteration() {
        if (areFreeDeliversAvailable() || areDishesAvailable()) return;
        Deliver deliver = getFreeDeliver();
        Optional<List<DeliveryOrder>> restaurantDeliveryOrders = processOrderByDeliver(deliver);
        restaurantDeliveryOrders.ifPresent(dList -> {
            Set<Long> request = restaurantDeliveryOrders.get()
                    .stream().map(DeliveryOrder::getId).collect(Collectors.toSet());
            restService.doPostRequest(portValues.getRestaurantPort() + GET_DONE_DELIVERY_URL, request, Set.class);
        });
    }

    private boolean areDishesAvailable() {
        boolean flag = orderQueue.isEmpty() || orderQueue.size() < MAX_ORDER_SIZE;
        if (flag) log.info("No orders available.");
        return !flag;
    }

    private boolean areFreeDeliversAvailable() {
        boolean flag = freeDelivers.isEmpty();
        if (flag) log.info("No free delivers.");
        return !flag;
    }

    // todo если очередь больше чем ? отослать сообщение, что сервес перегружет и нужно ждать дольше
}
