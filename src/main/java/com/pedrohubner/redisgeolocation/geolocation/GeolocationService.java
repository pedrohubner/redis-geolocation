package com.pedrohubner.redisgeolocation.geolocation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GeolocationService {
    private static final String KEY = "addresses";

    private final ReactiveRedisTemplate<String, Object> template;

    public Flux<Object> getNearestLocation(Double longitude, Double latitude, Double distance) {
        final var geoPoint = new Point(longitude, latitude);
        final var geoDistance = new Distance(distance, RedisGeoCommands.DistanceUnit.KILOMETERS);
        final var geoCircle = new Circle(geoPoint, geoDistance);

        return template
                .opsForGeo()
                .search(KEY, geoCircle)
                .map(geoResult -> geoResult.getContent().getName());
    }
}
