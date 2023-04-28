package com.tsid.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
//todo use record
public class DeliveryOrder {
    Long id;
    String address;
    String phone;
}