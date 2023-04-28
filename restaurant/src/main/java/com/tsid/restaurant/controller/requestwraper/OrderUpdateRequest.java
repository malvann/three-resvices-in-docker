package com.tsid.restaurant.controller.requestwraper;

import com.tsid.restaurant.model.OrderStatus;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class OrderUpdateRequest extends OrderCreateRequest {
    private Long id;
    private OrderStatus orderStatus;

    public OrderUpdateRequest(String address, String phone, @NonNull List<String> dishList) {
        super(address, phone, dishList);
    }
}
