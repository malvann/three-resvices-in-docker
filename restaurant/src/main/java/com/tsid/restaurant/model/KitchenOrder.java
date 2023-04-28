package com.tsid.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
//todo use record
public class KitchenOrder {
    private Long orderId;
    private List<String> dishList;
}