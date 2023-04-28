package com.tsid.kitchen.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class KitchenOrder {
    private Long orderId;
    private List<String> dishList;
}
