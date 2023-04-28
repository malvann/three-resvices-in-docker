package com.tsid.kitchen.controller.requestwraper;

import lombok.Data;

@Data
public class DishCreateRequest {
    private String name;
    private String recipe;
}
