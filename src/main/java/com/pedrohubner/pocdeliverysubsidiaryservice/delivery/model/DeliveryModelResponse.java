package com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model;

import com.pedrohubner.pocdeliverysubsidiaryservice.enums.DeliveryStatusEnum;
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
