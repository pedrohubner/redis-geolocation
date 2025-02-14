package com.pedrohubner.redisgeolocation.deliveryarea;

import com.pedrohubner.redisgeolocation.deliveryarea.mapper.DeliveryAreaResponseMapper;
import com.pedrohubner.redisgeolocation.deliveryarea.model.dto.CoordinatesDTO;
import com.pedrohubner.redisgeolocation.deliveryarea.model.request.DeliveryAreaRequest;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.CoordinateResponse;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.DeliveryAreaGeolocation;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.DeliveryAreaResponse;
import com.pedrohubner.redisgeolocation.enums.GeoShapeEnum;
import com.pedrohubner.redisgeolocation.subsidiary.mapper.SubsidiaryResponseMapper;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.domain.geo.BoundingBox;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.GeoShape;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryAreaService {
    private final ReactiveRedisTemplate<String, Long> geolocationTemplate;
    private final ReactiveRedisTemplate<String, SubsidiaryResponse> subsidiaryTemplate;
    private final ReactiveRedisTemplate<String, DeliveryAreaResponse> deliveryAreaTemplate;

    public Mono<DeliveryAreaResponse> createDeliveryArea(DeliveryAreaRequest request) {
        return Mono.just(request)
                .flatMap(this::getDeliveryAreas)
                .zipWhen(this::createDeliveryArea)
                .map(Tuple2::getT2);
    }

    private Mono<DeliveryAreaResponse> getDeliveryAreas(DeliveryAreaRequest deliveryAreaRequest) {
        final var subsidiaries = this.getSubsidiaries(deliveryAreaRequest);
        return Flux.fromIterable(subsidiaries)
                .flatMap(subsidiary -> subsidiary)
                .collectList()
                .map(subsidiaryResponses ->
                        DeliveryAreaResponseMapper.mapFrom(deliveryAreaRequest, subsidiaryResponses)
                );
    }

    private Set<Mono<SubsidiaryResponse>> getSubsidiaries(DeliveryAreaRequest deliveryAreaRequest) {
        return deliveryAreaRequest.subsidiaries().stream()
                .map(this::findSubsidiaryByCode)
                .collect(Collectors.toSet());
    }

    private Mono<SubsidiaryResponse> findSubsidiaryByCode(Long subsidiaryCode) {
        return subsidiaryTemplate.opsForValue()
                .get(String.valueOf(subsidiaryCode));
    }

    private Mono<DeliveryAreaResponse> createDeliveryArea(DeliveryAreaResponse deliveryAreaToBeSaved) {
        return deliveryAreaTemplate.opsForValue()
                .set(deliveryAreaToBeSaved.id(), deliveryAreaToBeSaved)
                .flatMap(response -> deliveryAreaTemplate.opsForValue().get(deliveryAreaToBeSaved.id()))
                .zipWhen(this::createAreaGeolocation)
                .map(Tuple2::getT1);
    }

    private Mono<Long> createAreaGeolocation(DeliveryAreaResponse area) {
        final var coordinates = area.subsidiaries().stream()
                .map(DeliveryAreaService::createGeolocationCoordinate)
                .toList();

        return geolocationTemplate.opsForGeo()
                .add(area.name(), coordinates);
    }

    private static RedisGeoCommands.GeoLocation<Long> createGeolocationCoordinate(SubsidiaryResponse subsidiary) {
        final var point = new Point(subsidiary.coordinate().longitude(), subsidiary.coordinate().latitude());
        return new RedisGeoCommands.GeoLocation<>(subsidiary.code(), point);
    }

    public Mono<Object> getNearestLocation(CoordinatesDTO dto) {
        final var geoPoint = new Point(dto.longitude(), dto.latitude());
        final var geoReference = GeoReference.<Long>fromCoordinate(geoPoint);
        final var geoShape = this.getGeoShape(dto);
        final var command = RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                .sortAscending()
                .includeCoordinates()
                .includeDistance();

        return geolocationTemplate
                .opsForGeo()
                .search(dto.key(), geoReference, geoShape, command)
                .collectList()
                .map(geoResults -> {
                    final var subsidiaries = geoResults.stream()
                            .map(SubsidiaryResponseMapper::mapFrom)
                            .toList();

                    final var referencePoint = CoordinateResponse.builder()
                            .longitude(dto.longitude())
                            .latitude(dto.latitude())
                            .build();

                    return new DeliveryAreaGeolocation(referencePoint, subsidiaries);
                });
    }

    private GeoShape getGeoShape(CoordinatesDTO dto) {
        final var distanceUnit = RedisGeoCommands.DistanceUnit.KILOMETERS;

        if (GeoShapeEnum.BOX.equals(dto.shape()))
            return GeoShape.byBox(new BoundingBox(dto.width(), dto.height(), distanceUnit));

        return GeoShape.byRadius(new Distance(dto.distance(), distanceUnit));
    }
}
