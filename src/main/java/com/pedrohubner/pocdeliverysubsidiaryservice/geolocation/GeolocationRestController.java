package com.pedrohubner.pocdeliverysubsidiaryservice.geolocation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class GeolocationRestController {
    private final GeolocationService service;

    @GetMapping("/geolocation")
    public Flux<Object> getGeolocation(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude,
            @RequestParam("distance") Double distance
    ) {
        return service.getNearestLocation(longitude, latitude, distance);
    }
}
