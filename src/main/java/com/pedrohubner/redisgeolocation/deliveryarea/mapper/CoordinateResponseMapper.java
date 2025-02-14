package com.pedrohubner.redisgeolocation.deliveryarea.mapper;

import com.pedrohubner.redisgeolocation.deliveryarea.model.request.CoordinatesRequest;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.CoordinateResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoordinateResponseMapper {
    public static CoordinateResponse mapFrom(CoordinatesRequest request) {
        return CoordinateResponse.builder()
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
    }

    public static CoordinateResponse mapFrom(GeoResult<RedisGeoCommands.GeoLocation<Long>> geoResult) {
        return CoordinateResponse.builder()
                .longitude(geoResult.getContent().getPoint().getX())
                .latitude(geoResult.getContent().getPoint().getY())
                .distance(geoResult.getDistance().getValue())
                .distanceUnit(geoResult.getDistance().getUnit())
                .build();
    }
}
