package com.tsid.delivery.controller;

import com.tsid.delivery.controller.requestwraper.DeliverCreateRequest;
import com.tsid.delivery.controller.requestwraper.DeliverUpdateRequest;
import com.tsid.delivery.model.Deliver;
import com.tsid.delivery.model.DeliveryOrder;
import com.tsid.delivery.repository.DeliverRepository;
import com.tsid.delivery.service.DeliveryClub;
import exception.DataFormatException;
import exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static constant.UrlPath.DeliveryUrl.DOM_URL;
import static constant.UrlPath.DeliveryUrl.RECEIVE_ORDERS_URL;

@RestController
@AllArgsConstructor
public class DeliveryController {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final DeliveryClub delivery;
    private final DeliverRepository deliverRepository;

    @PostMapping(RECEIVE_ORDERS_URL)
    public ResponseEntity receiveRestaurantOrders(@RequestBody List<DeliveryOrder> request) {
        delivery.receiveRestaurantOrders(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(DOM_URL)
    public ResponseEntity<List<Deliver>> findAllDelivers() {
        return ResponseEntity.ok(deliverRepository.findAll());
    }

    @GetMapping(DOM_URL + "/{id}")
    public ResponseEntity<Deliver> findDeliverById(@PathVariable Long id) {
        return ResponseEntity.ok(deliverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString())));
    }

    @PostMapping(DOM_URL + "/create")
    public ResponseEntity<Deliver> createDeliver(@RequestBody DeliverCreateRequest request) {
        Deliver deliver = fillByRequest(new Deliver(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deliverRepository.save(deliver));
    }

    @PutMapping(DOM_URL + "/update")
    public ResponseEntity<Deliver> updateDeliver(@RequestBody DeliverUpdateRequest request) {
        Deliver deliver = deliverRepository.findById(request.getId())
                .orElseThrow(() -> new DataFormatException("No such element"));
        fillByRequest(deliver, request);
        return ResponseEntity.ok(deliverRepository.save(deliver));
    }

    @GetMapping(DOM_URL + "/delete/{id}")
    public ResponseEntity<Long> deleteDeliver(@PathVariable Long id) {
        deliverRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    private static Deliver fillByRequest(Deliver deliver, DeliverCreateRequest request) {
        if (deliver.getId() == null) {
            deliver.setId(RANDOM.nextLong());
            deliver.setBusy(false);
            deliver.setCreated(new Timestamp(new Date().getTime()));
        } else {
            deliver.setUpdated(new Timestamp(new Date().getTime()));
        }
        deliver.setName(request.getName());
        deliver.setContact(request.getContact());
        return deliver;
    }
}
