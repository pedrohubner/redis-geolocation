package com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model;

import com.pedrohubner.pocdeliverysubsidiaryservice.enums.DeliveryStatusEnum;
import lombok.NonNull;

public record DeliveryModelRequest(
        @NonNull
        Integer type,
        @NonNull
        Integer method,
        @NonNull
        DeliveryStatusEnum status,
        @NonNull
        String description
) {
}
