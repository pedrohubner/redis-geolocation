package com.pedrohubner.pocdeliverysubsidiaryservice.stub;

import com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model.DeliveryModelResponse;
import com.pedrohubner.pocdeliverysubsidiaryservice.enums.DeliveryStatusEnum;

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
