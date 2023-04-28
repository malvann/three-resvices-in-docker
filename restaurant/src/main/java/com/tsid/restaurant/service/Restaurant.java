package com.tsid.restaurant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.tsid.restaurant.configurations.PortValues;
import com.tsid.restaurant.model.DeliveryOrder;
import com.tsid.restaurant.model.KitchenOrder;
import com.tsid.restaurant.model.Order;
import com.tsid.restaurant.model.OrderStatus;
import com.tsid.restaurant.repository.OrderRepository;
import component.RestService;
import constant.UrlPath;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static constant.UrlPath.KitchenUrl.DAY_DISH_SET;

@Service
@AllArgsConstructor
@Slf4j
public class Restaurant extends AbstractScheduledService {
    private final PortValues portValues;
    private final OrderRepository orderRepository;
    private final RestService restService;

    private Set<String> dayDishSet;
    private final List<KitchenOrder> kitchenOrders = Collections.synchronizedList(new LinkedList<>());
    private final List<DeliveryOrder> deliveryOrders = Collections.synchronizedList(new LinkedList<>());

    //принимать заказ от потребителя, проверить есть ли такой в репе (запрос в Kitchen), класть в kitchenOrders и бд,
    //      статус (CREATED), отправить смс? заказчику с сылкой на страницу, где по id доставать статус заказа
    public Order createOrder(Order order) throws JsonProcessingException {
        List<String> validDishes = Order.getValueFromJson(order.getDishList()).stream()
                .filter(dishName -> dayDishSet.contains(dishName)).toList();
        log.info("dayDishSet:" + dayDishSet);
        log.info("order:" + order);
        order.setDishList(Order.getJsonRef(validDishes));
        orderRepository.save(order);
        kitchenOrders.add(new KitchenOrder(order.getId(), Order.getValueFromJson(order.getDishList())));
        log.info("Order {} created.", order);
        return order;
    }

    // раз в 5мин отправлять заказы в Kitchen, менять статус заказа на COOKING
    public void sendKitchenOrders() {
        List<KitchenOrder> kOrderList = new ArrayList<>(kitchenOrders);
        kitchenOrders.clear();
        createKitchenOrderRequest(kOrderList);
        restService.doPostRequest(portValues.getKitchenPort() + UrlPath.KitchenUrl.RECEIVE_ORDERS_URL,
                kOrderList, List.class);
        log.info("Orders {} sent to kitchen.", kOrderList);
    }

    private void createKitchenOrderRequest(List<KitchenOrder> kOrderList) {
        kOrderList.forEach(kOrder -> {
            Order order = orderRepository.getReferenceById(kOrder.getOrderId());
            //todo need to create orderRepository.update()
            order.setOrderStatus(OrderStatus.COOKING);
            orderRepository.save(order);
        });
    }

    // раз в 5мин отправлять заказы в Deliver, менять статус заказа на DELIVERING
    public void sendDeliverOrders() {
        List<DeliveryOrder> dOrders;
        synchronized (deliveryOrders) {
            dOrders = new ArrayList<>(deliveryOrders);
            deliveryOrders.clear();
        }
        createDeliveryOrderRequest(dOrders);
        restService.doPostRequest(portValues.getDeliveryPort() + UrlPath.KitchenUrl.RECEIVE_ORDERS_URL,
                dOrders, List.class);
        log.info("Orders {} sent to delivery.", dOrders);
    }

    private void createDeliveryOrderRequest(List<DeliveryOrder> dOrderList) {
        dOrderList.forEach(dOrder -> {
            Order order = orderRepository.getById(dOrder.getId());
            //todo need to create orderRepository.update()
            order.setOrderStatus(OrderStatus.DELIVERING);
            orderRepository.save(order);
        });
    }

    //пинимать сообщение от Kitchen, добавлять в deliveryOrders
    public synchronized void acceptKitchenOrder(Long orderId) {
        Order order = orderRepository.getById(orderId);
        DeliveryOrder deliveryOrder = new DeliveryOrder(order.getId(), order.getAddress(), order.getPhone());
        deliveryOrders.add(deliveryOrder);
    }

    //пинимать сообщение от Delivery, менять статус заказа на DONE
    public synchronized void acceptDeliveryOrder(Set<Long> orderIdSet) {
        orderIdSet.forEach(oId -> {
            Order done = orderRepository.getReferenceById(oId);
            done.setOrderStatus(OrderStatus.DONE);
            orderRepository.save(done);
        });
    }

    public void requestDayDishSet() {
        ResponseEntity<Set> entity = restService.doGetRequest(portValues.getKitchenPort() + DAY_DISH_SET, Set.class);
        //todo need to throw Exception
        Set<String> dishNamesSet = Objects.requireNonNull(entity.getBody());
        dayDishSet = new HashSet<>(dishNamesSet);
        log.info("Day dish set is updated {}", dayDishSet);
    }

    @Override
    public void startUp() {
        requestDayDishSet();
    }

    @Override
    protected @NonNull Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.SECONDS);
    }

    protected void runOneIteration() {
        sendKitchenOrders();
        sendDeliverOrders();
    }
}
