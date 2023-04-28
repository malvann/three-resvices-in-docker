package com.tsid.restaurant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsid.restaurant.controller.requestwraper.OrderCreateRequest;
import com.tsid.restaurant.controller.requestwraper.OrderUpdateRequest;
import com.tsid.restaurant.model.Order;
import com.tsid.restaurant.model.OrderStatus;
import com.tsid.restaurant.repository.OrderRepository;
import com.tsid.restaurant.service.Restaurant;
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

import static constant.UrlPath.RestaurantUrl.*;

@RestController
@AllArgsConstructor
@Slf4j
public class RestaurantController {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final Restaurant restaurant;
    private final OrderRepository orderRepository;

    @PostMapping(GET_DONE_KITCHEN_URL)
    public ResponseEntity<Long> getKitchenOrder(@RequestBody Long orderId) {
        restaurant.acceptKitchenOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderId);
    }

    @GetMapping(GET_DONE_DELIVERY_URL)
    public ResponseEntity<Set<Long>> getDeliveryOrder(@RequestBody Set<Long> orderIdSet) {
        restaurant.acceptDeliveryOrder(orderIdSet);
        return ResponseEntity.status(HttpStatus.OK).body(orderIdSet);
    }

    @GetMapping(DOM_URL)
    public ResponseEntity<List<Order>> findAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping(ORDER_STATUS_URL + "/{id}")
    public ResponseEntity<String> getOrderStatus(@PathVariable Long id) {
        return ResponseEntity.ok(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString())).getOrderStatus().toString());
    }

    @PostMapping(DOM_URL + "/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateRequest request) {
        try {
            Order order = fillByRequest(new Order(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(restaurant.createOrder(order));
        } catch (JsonProcessingException e) {
            log.warn("Json conversion: ", e);
        }
        //todo need to create GlobalExceptionHandler
        return ResponseEntity.badRequest().body(null);
    }

    //manually by administrator
    @PutMapping(DOM_URL + "/update")
    public ResponseEntity<Order> updateOrder(@RequestBody OrderUpdateRequest request) {
        try {
            Order order = orderRepository.findById(request.getId())
                    .orElseThrow(() -> new DataFormatException("No such element"));
            fillByRequest(order, request);
            return ResponseEntity.ok(orderRepository.save(order));
        } catch (JsonProcessingException e) {
            log.warn("Json conversion: ", e);
        }
        return ResponseEntity.badRequest().body(null);
    }

    //manually by administrator
    @GetMapping(DOM_URL + "/delete/{id}")
    public ResponseEntity<Long> deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    private static Order fillByRequest(Order order, OrderCreateRequest request) throws JsonProcessingException {
        if (order.getId() == null) {
            order.setId(RANDOM.nextLong(0, Long.MAX_VALUE));
            order.setOrderStatus(OrderStatus.CREATED);
            order.setCreated(new Timestamp(new Date().getTime()));
        } else {
            order.setUpdated(new Timestamp(new Date().getTime()));
        }
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());
        order.setDishList(Order.getJsonRef(request.getDishList()));
        return order;
    }
}
