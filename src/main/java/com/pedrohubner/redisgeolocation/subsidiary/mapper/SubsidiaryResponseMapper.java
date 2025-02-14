package com.pedrohubner.redisgeolocation.subsidiary.mapper;

import com.pedrohubner.redisgeolocation.deliveryarea.mapper.CoordinateResponseMapper;
import com.pedrohubner.redisgeolocation.subsidiary.model.request.SubsidiaryRequest;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubsidiaryResponseMapper {
    public static SubsidiaryResponse mapFrom(SubsidiaryRequest request) {
        return SubsidiaryResponse.builder()
                .code(request.code())
                .name(request.name())
                .coordinate(CoordinateResponseMapper.mapFrom(request.coordinate()))
                .build();
    }

    public static SubsidiaryResponse mapFrom(GeoResult<RedisGeoCommands.GeoLocation<Long>> geoResult) {
        return SubsidiaryResponse.builder()
                .code(geoResult.getContent().getName())
                .coordinate(CoordinateResponseMapper.mapFrom(geoResult))
                .build();
    }
}
