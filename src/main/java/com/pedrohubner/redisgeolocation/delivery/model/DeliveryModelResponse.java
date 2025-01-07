package com.pedrohubner.redisgeolocation.delivery.model;

import com.pedrohubner.redisgeolocation.enums.DeliveryStatusEnum;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deliveries")
public record DeliveryModelResponse(
        String id,
        int type,
        int method,
        DeliveryStatusEnum status,
        String description
) {

    public DeliveryModelResponse(int type, int method, DeliveryStatusEnum status, String description) {
        this(null, type, method, status, description);
    }

}
