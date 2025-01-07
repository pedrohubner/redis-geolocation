package com.pedrohubner.redisgeolocation.delivery;

import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelRequest;
import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelResponse;
import com.pedrohubner.redisgeolocation.enums.DeliveryStatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/deliveries/models")
public class DeliveryRestController {

    private final DeliveryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DeliveryModelResponse> createDelivery(@RequestBody DeliveryModelRequest request) {
        return service.createDelivery(request);
    }

    @GetMapping
    public Mono<ResponseEntity<List<DeliveryModelResponse>>> getDeliveries(
            @RequestParam(required = false, defaultValue = "ACTIVE") List<String> status
    ) {
        return service.getDeliveries(status)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PutMapping("/{deliveryId}")
    public Mono<DeliveryModelResponse> updateDelivery(
            @PathVariable("deliveryId") String deliveryId, @RequestBody DeliveryModelRequest request
    ) {
        return service.updateDelivery(deliveryId, request);
    }

    @PatchMapping("/{deliveryId}")
    public Mono<DeliveryModelResponse> disableDelivery(
            @PathVariable("deliveryId") String deliveryId, @RequestParam DeliveryStatusEnum status
    ) {
        return service.disableDelivery(deliveryId, status);
    }

}
