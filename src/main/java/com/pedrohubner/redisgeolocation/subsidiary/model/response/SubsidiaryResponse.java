package com.pedrohubner.redisgeolocation.subsidiary.model.response;

import com.pedrohubner.redisgeolocation.deliveryarea.model.response.CoordinateResponse;
import lombok.Builder;

@Builder
public record SubsidiaryResponse(
        Long code,
        String name,
        CoordinateResponse coordinate
) {
}
