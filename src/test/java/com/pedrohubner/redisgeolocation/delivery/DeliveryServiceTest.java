package com.pedrohubner.redisgeolocation.delivery;

import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelResponse;
import com.pedrohubner.redisgeolocation.stub.DeliveryModelResponseStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    private static final String CACHE_KEY = "delivery-subsidiary-service:delivery-models";

    @Mock
    private DeliveryRepository repository;
    @Mock
    private ReactiveRedisTemplate<String, DeliveryModelResponse> reactiveRedisTemplate;
    @Mock
    private ReactiveRedisTemplate<String, String> deliveryIdRedisTemplate;
    @Mock
    private ReactiveValueOperations<String, DeliveryModelResponse> reactiveValueOperations;
    @Mock
    private ReactiveSetOperations<String, String> reactiveSetOperations;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        Mockito.when(deliveryIdRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
        deliveryService = new DeliveryService(
                repository,
                reactiveRedisTemplate,
                deliveryIdRedisTemplate
        );
    }

    @Test
    public void shouldGetDeliveriesFromRedisWhenRedisHasCache() {
        final var statusList = Arrays.asList("status1", "status2");
        final var delivery1 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery2 = DeliveryModelResponseStub.deliveryModelTwo();
        final var formattedKeyDelivery1 = CACHE_KEY.concat(":").concat(delivery1.id());
        final var formattedKeyDelivery2 = CACHE_KEY.concat(":").concat(delivery2.id());

        Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        Mockito.when(reactiveSetOperations.members(CACHE_KEY)).thenReturn(Flux.just(delivery1.id(), delivery2.id()));
        Mockito.when(reactiveValueOperations.get(formattedKeyDelivery1)).thenReturn(Mono.just(delivery1));
        Mockito.when(reactiveValueOperations.get(formattedKeyDelivery2)).thenReturn(Mono.just(delivery2));

        StepVerifier.create(deliveryService.getDeliveries(statusList))
                .expectNext(delivery1)
                .expectNext(delivery2)
                .verifyComplete();

        Mockito.verify(reactiveSetOperations, Mockito.times(1)).members(CACHE_KEY);
        Mockito.verify(reactiveValueOperations, Mockito.times(1)).get(formattedKeyDelivery1);
        Mockito.verify(reactiveValueOperations, Mockito.times(1)).get(formattedKeyDelivery2);
    }

    @Test
    public void shouldGetDeliveriesWhenMongoHasDeliveries() {
        final var statusList = Arrays.asList("status1", "status2");
        final var delivery1 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery2 = DeliveryModelResponseStub.deliveryModelTwo();
        final var formattedKeyDelivery1 = CACHE_KEY.concat(":").concat(delivery1.id());
        final var formattedKeyDelivery2 = CACHE_KEY.concat(":").concat(delivery2.id());

        Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        Mockito.when(reactiveSetOperations.members(CACHE_KEY)).thenReturn(Flux.empty());
        Mockito.when(repository.findByStatusIn(statusList)).thenReturn(Flux.just(delivery1, delivery2));
        Mockito.when(reactiveValueOperations.set(formattedKeyDelivery1, delivery1)).thenReturn(Mono.just(true));
        Mockito.when(reactiveValueOperations.set(formattedKeyDelivery2, delivery2)).thenReturn(Mono.just(true));
        Mockito.when(reactiveSetOperations.add(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(1L));

        StepVerifier.create(deliveryService.getDeliveries(statusList))
                .expectNext(delivery1)
                .expectNext(delivery2)
                .verifyComplete();

        final var delivery11 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery21 = DeliveryModelResponseStub.deliveryModelTwo();
        Mockito.verify(reactiveSetOperations, Mockito.times(1)).add(CACHE_KEY, delivery11.id());
        Mockito.verify(reactiveSetOperations, Mockito.times(1)).add(CACHE_KEY, delivery21.id());
        Mockito.verify(reactiveValueOperations, Mockito.times(1)).set(formattedKeyDelivery1, delivery11);
        Mockito.verify(reactiveValueOperations, Mockito.times(1)).set(formattedKeyDelivery2, delivery21);
    }

    @Test
    public void shouldReturnNoContentExceptionWhenMongoHasNoValues() {
        final var statusList = Arrays.asList("status1", "status2");

        Mockito.when(reactiveSetOperations.members(CACHE_KEY)).thenReturn(Flux.empty());
        Mockito.when(repository.findByStatusIn(statusList)).thenReturn(Flux.empty());

        StepVerifier.create(deliveryService.getDeliveries(statusList))
                .verifyComplete();
    }

}