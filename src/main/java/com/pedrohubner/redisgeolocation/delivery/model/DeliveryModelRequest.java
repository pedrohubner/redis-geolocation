package com.pedrohubner.redisgeolocation.delivery.model;

import com.pedrohubner.redisgeolocation.enums.DeliveryStatusEnum;
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
