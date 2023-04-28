package com.tsid.restaurant.controller.requestwraper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderCreateRequest {
    private String address;
    private String phone;
    @NonNull
    private List<String> dishList;
}
