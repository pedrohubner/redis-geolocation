package com.pedrohubner.redisgeolocation.deliveryarea.mapper;

import com.pedrohubner.redisgeolocation.deliveryarea.model.request.DeliveryAreaRequest;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.DeliveryAreaResponse;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryAreaResponseMapper {
    public static DeliveryAreaResponse mapFrom(DeliveryAreaRequest request, List<SubsidiaryResponse> subsidiaries) {
        return DeliveryAreaResponse.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .subsidiaries(new HashSet<>(subsidiaries))
                .build();
    }
}
