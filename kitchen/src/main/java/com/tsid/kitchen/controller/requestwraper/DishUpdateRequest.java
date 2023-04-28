package com.tsid.kitchen.controller.requestwraper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DishUpdateRequest extends DishCreateRequest{
    private Long id;
    private boolean isAvailable;
}
