package com.pedrohubner.pocdeliverysubsidiaryservice.delivery;

import com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model.DeliveryModelResponse;
import com.pedrohubner.pocdeliverysubsidiaryservice.stub.DeliveryModelResponseStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    private static final String CACHE_KEY = "delivery-subsidiary-service:delivery-models";
    private static final int FIRST_POSITION = 0;
    private static final int LAST_POSITION = -1;

    @Mock
    private DeliveryRepository repository;
    @Mock
    private ReactiveRedisTemplate<String, DeliveryModelResponse> reactiveRedisTemplate;
    @Mock
    private ReactiveListOperations<String, DeliveryModelResponse> reactiveListOperations;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        Mockito.when(reactiveRedisTemplate.opsForList()).thenReturn(reactiveListOperations);
        deliveryService = new DeliveryService(repository, reactiveRedisTemplate);
    }

    @Test
    public void shouldGetDeliveriesWhenMongoHasDeliveries() {
        final var statusList = Arrays.asList("status1", "status2");
        final var delivery1 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery2 = DeliveryModelResponseStub.deliveryModelTwo();

        Mockito.when(repository.findByStatusIn(statusList)).thenReturn(Flux.just(delivery1, delivery2));
        Mockito.when(reactiveListOperations.range(CACHE_KEY, FIRST_POSITION, LAST_POSITION)).thenReturn(Flux.empty());
        Mockito.when(reactiveListOperations.rightPushAll(CACHE_KEY, List.of(delivery1, delivery2)))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(deliveryService.getDeliveries(statusList))
                .expectNext(delivery1)
                .expectNext(delivery2)
                .verifyComplete();

        final var delivery11 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery21 = DeliveryModelResponseStub.deliveryModelTwo();
        Mockito.verify(reactiveListOperations, Mockito.times(1)).rightPushAll(CACHE_KEY, List.of(delivery11, delivery21));
    }

    @Test
    public void shouldReturnNoContentExceptionWhenMongoHasNoValues() {
        final var statusList = Arrays.asList("status1", "status2");

        Mockito.when(reactiveListOperations.range(CACHE_KEY, FIRST_POSITION, LAST_POSITION)).thenReturn(Flux.empty());
        Mockito.when(repository.findByStatusIn(statusList)).thenReturn(Flux.empty());

        StepVerifier.create(deliveryService.getDeliveries(statusList))
                .verifyComplete();
    }

}