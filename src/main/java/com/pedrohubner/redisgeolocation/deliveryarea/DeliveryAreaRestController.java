package com.pedrohubner.redisgeolocation.deliveryarea;

import com.pedrohubner.redisgeolocation.deliveryarea.model.dto.CoordinatesDTO;
import com.pedrohubner.redisgeolocation.deliveryarea.model.request.DeliveryAreaRequest;
import com.pedrohubner.redisgeolocation.deliveryarea.model.response.DeliveryAreaResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/delivery-areas")
public class DeliveryAreaRestController {
    private final DeliveryAreaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DeliveryAreaResponse> createDeliveryArea(@RequestBody DeliveryAreaRequest request) {
        return service.createDeliveryArea(request);
    }

    @GetMapping
    public Mono<Object> getDeliveryAreasByCoordinate(
            CoordinatesDTO dto
    ) {
        return service.getNearestLocation(dto);
    }
//
//    @PatchMapping("/{deliveryAreaId}")
//    public Mono<DeliveryAreaResponse> removeSubsidiariesFromDeliveryArea(
//            @PathVariable("deliveryAreaId") String deliveryAreaId, @RequestParam Set<Long> subsidiaryId
//    ) {
//        System.out.println(deliveryAreaId + " - " + subsidiaryId);
//        return Mono.empty();
//    }
//
//    @DeleteMapping("/{deliveryAreaId}")
//    public Mono<DeliveryAreaResponse> deleteDeliveryArea(
//            @PathVariable("deliveryAreaId") String deliveryAreaId
//    ) {
//        System.out.println(deliveryAreaId);
//        return Mono.empty();
//    }
}
