package com.tsid.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryOrder {
    Long id;
    String address;
    String phone;
}
