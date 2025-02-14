package com.pedrohubner.redisgeolocation.deliveryarea.model.request;

import lombok.NonNull;

import java.util.Set;

public record DeliveryAreaRequest(
        String name,
        @NonNull
        Set<Long> subsidiaries
) {
}
