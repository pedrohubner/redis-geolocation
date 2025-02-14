package com.pedrohubner.redisgeolocation.deliveryarea.model.response;

import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;

import java.util.List;

public record DeliveryAreaGeolocation(
        CoordinateResponse referencePoint,
        List<SubsidiaryResponse> subsidiaries
) {
}
