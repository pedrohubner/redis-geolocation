package com.pedrohubner.redisgeolocation.subsidiary.model.request;

import com.pedrohubner.redisgeolocation.deliveryarea.model.request.CoordinatesRequest;

public record SubsidiaryRequest(
        Long code,
        String name,
        CoordinatesRequest coordinate
) {
}
