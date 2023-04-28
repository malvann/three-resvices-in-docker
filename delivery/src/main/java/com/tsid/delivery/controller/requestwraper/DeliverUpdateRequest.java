package com.tsid.delivery.controller.requestwraper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeliverUpdateRequest extends DeliverCreateRequest{
    private Long id;
    private boolean busy;
}
