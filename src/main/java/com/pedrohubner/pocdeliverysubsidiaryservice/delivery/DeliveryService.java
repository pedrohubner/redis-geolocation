package com.pedrohubner.pocdeliverysubsidiaryservice.delivery;

import com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model.DeliveryModelRequest;
import com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model.DeliveryModelResponse;
import com.pedrohubner.pocdeliverysubsidiaryservice.enums.DeliveryStatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class DeliveryService {

    private static final String CACHE_KEY = "delivery-subsidiary-service:delivery-models";

    private final DeliveryRepository repository;
    private final ReactiveRedisTemplate<String, DeliveryModelResponse> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, String> deliveryIdRedisTemplate;

    public Mono<DeliveryModelResponse> createDelivery(DeliveryModelRequest request) {
        final var entity = getEntity(request);
        return repository.save(entity)
                .doOnNext(this::insertChangeOnCache);
    }

    public Flux<DeliveryModelResponse> getDeliveries(List<String> status) {
        return getCachedDeliveries()
                .switchIfEmpty(Mono.defer(
                        () -> getDeliveriesAndSetToCache(status)).flatMapMany(Flux::fromIterable)
                );
    }

    private Flux<DeliveryModelResponse> getCachedDeliveries() {
        return deliveryIdRedisTemplate.opsForSet().members(CACHE_KEY)
                .flatMap(this::getDeliveryModelFromCache)
                .map(value -> value);
    }

    private Mono<DeliveryModelResponse> getDeliveryModelFromCache(String deliveryId) {
        final var formattedKey = getFormattedKey(deliveryId);
        return reactiveRedisTemplate.opsForValue().get(formattedKey);
    }

    private Mono<List<DeliveryModelResponse>> getDeliveriesAndSetToCache(List<String> status) {
        return repository.findByStatusIn(status)
                .collectList()
                .filter(deliveries -> !deliveries.isEmpty())
                .doOnNext(this::saveDeliveryModelsInCache)
                .flatMap(Mono::just)
                .switchIfEmpty(Mono.defer(Mono::empty));
    }

    private void saveDeliveryModelsInCache(List<DeliveryModelResponse> deliveries) {
        for (DeliveryModelResponse delivery : deliveries) {
            insertChangeOnCache(delivery);
        }
    }

    public Mono<DeliveryModelResponse> updateDelivery(String deliveryId, DeliveryModelRequest request) {
        return repository.findById(deliveryId)
                .flatMap(delivery -> {
                    final var newDelivery = getEntity(deliveryId, request);
                    return repository.save(newDelivery)
                            .doOnNext(this::insertChangeOnCache);
                });
    }

    public Mono<DeliveryModelResponse> disableDelivery(String deliveryId, DeliveryStatusEnum status) {
        return repository.findById(deliveryId)
                .flatMap(delivery -> {
                    final var newDelivery = getEntity(delivery, status);
                    return repository.save(newDelivery)
                            .doOnNext(this::insertChangeOnCache);
                });
    }

    private void insertChangeOnCache(DeliveryModelResponse delivery) {
        final var formattedKey = getFormattedKey(delivery.id());
        reactiveRedisTemplate.opsForValue().set(formattedKey, delivery).subscribe();
        deliveryIdRedisTemplate.opsForSet().add(CACHE_KEY, delivery.id()).subscribe();
    }

    private String getFormattedKey(String deliveryId) {
        return String.format(CACHE_KEY.concat(":%s"), deliveryId);
    }

    private static DeliveryModelResponse getEntity(DeliveryModelRequest request) {
        return new DeliveryModelResponse(request.type(), request.method(), request.status(), request.description());
    }

    private static DeliveryModelResponse getEntity(String id, DeliveryModelRequest request) {
        return new DeliveryModelResponse(id, request.type(), request.method(), request.status(), request.description());
    }

    private static DeliveryModelResponse getEntity(DeliveryModelResponse delivery, DeliveryStatusEnum status) {
        return new DeliveryModelResponse(
                delivery.id(),
                delivery.type(),
                delivery.method(),
                status,
                delivery.description()
        );
    }

}
