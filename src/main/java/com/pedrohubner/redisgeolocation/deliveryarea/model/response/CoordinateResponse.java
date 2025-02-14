package com.pedrohubner.redisgeolocation.deliveryarea.model.response;

import lombok.Builder;

@Builder
public record CoordinateResponse(
        Double longitude,
        Double latitude,
        Double distance,
        String distanceUnit
) {
}
