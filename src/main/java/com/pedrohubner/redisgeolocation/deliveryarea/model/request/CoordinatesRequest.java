package com.pedrohubner.redisgeolocation.deliveryarea.model.request;

public record CoordinatesRequest(
        Double longitude,
        Double latitude,
        Double width,
        Double height
) {
}
