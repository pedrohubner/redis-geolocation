package com.pedrohubner.redisgeolocation.stub;

import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelResponse;
import com.pedrohubner.redisgeolocation.enums.DeliveryStatusEnum;

public class DeliveryModelResponseStub {

    public static DeliveryModelResponse deliveryModelOne() {
        return new DeliveryModelResponse(
                "1",
                1,
                1,
                DeliveryStatusEnum.ACTIVE,
                "Delivery 1 description"
        );
    }

    public static DeliveryModelResponse deliveryModelTwo() {
        return new DeliveryModelResponse(
                "2",
                2,
                2,
                DeliveryStatusEnum.INACTIVE,
                "Delivery 2 description"
        );
    }

}
