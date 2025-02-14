package com.pedrohubner.redisgeolocation.deliveryarea.model.response;

import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Builder
public record DeliveryAreaResponse(
        @Id
        String id,
        String name,
        Set<SubsidiaryResponse> subsidiaries
) {
}
