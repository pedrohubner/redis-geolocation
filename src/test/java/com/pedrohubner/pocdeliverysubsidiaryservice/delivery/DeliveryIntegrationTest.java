package com.pedrohubner.pocdeliverysubsidiaryservice.delivery;

import com.pedrohubner.pocdeliverysubsidiaryservice.config.MongoDBServerConfig;
import com.pedrohubner.pocdeliverysubsidiaryservice.config.RedisServerConfig;
import com.pedrohubner.pocdeliverysubsidiaryservice.delivery.model.DeliveryModelResponse;
import com.pedrohubner.pocdeliverysubsidiaryservice.stub.DeliveryModelResponseStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
@ImportTestcontainers(value = {MongoDBServerConfig.class, RedisServerConfig.class})
public class DeliveryIntegrationTest {

    private static final String CACHE_KEY = "delivery-subsidiary-service:delivery-models";

    @Autowired
    WebTestClient webTestClient;
    @SpyBean
    @Autowired
    DeliveryRepository deliveryRepository;
    @SpyBean
    @Autowired
    ReactiveRedisTemplate<String, DeliveryModelResponse> reactiveRedisTemplate;
    @Autowired
    ReactiveRedisTemplate<String, String> deliveryIdRedisTemplate;

    @Test
    public void shouldGetDeliveriesFromMongoDBAndThenSaveItOnRedisWhenRedisDoesNotHaveCache() {
        final var uri = "/v1/deliveries/models";
        final var statusList = List.of("ACTIVE");
        final var cacheKey = String.format(CACHE_KEY.concat("%s"), statusList);
        final var delivery1 = DeliveryModelResponseStub.deliveryModelOne();
        final var expected = List.of(delivery1);

        deliveryRepository.saveAll(List.of(delivery1)).collectList().block();

        webTestClient
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DeliveryModelResponse.class)
                .consumeWith(response -> {
                    final var responseBody = response.getResponseBody();
                    Assertions.assertEquals(expected, responseBody);
                });

        Mockito.verify(reactiveRedisTemplate, Mockito.times(1)).opsForValue();
        Mockito.verify(deliveryRepository, Mockito.times(1)).findByStatusIn(statusList);
        deleteMongoAndRedisInfo(cacheKey);
    }

    @Test
    public void shouldGetDeliveriesFromRedisWhenRedisDoesHaveCache() {
        final var uri = "/v1/deliveries/models";
        final var delivery1 = DeliveryModelResponseStub.deliveryModelOne();
        final var delivery2 = DeliveryModelResponseStub.deliveryModelTwo();
        final var expected = List.of(delivery1, delivery2);
        final var formattedKeyDelivery1 = CACHE_KEY.concat(":").concat(delivery1.id());
        final var formattedKeyDelivery2 = CACHE_KEY.concat(":").concat(delivery2.id());

        deliveryIdRedisTemplate.opsForSet().add(CACHE_KEY, delivery1.id()).subscribe();
        deliveryIdRedisTemplate.opsForSet().add(CACHE_KEY, delivery2.id()).subscribe();
        reactiveRedisTemplate.opsForValue().set(formattedKeyDelivery1, delivery1).subscribe();
        reactiveRedisTemplate.opsForValue().set(formattedKeyDelivery2, delivery2).subscribe();

        webTestClient
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DeliveryModelResponse.class)
                .consumeWith(response -> {
                    final var responseBody = response.getResponseBody();
                    Assertions.assertEquals(expected, responseBody);
                });

        Mockito.verify(reactiveRedisTemplate, Mockito.times(4)).opsForValue();

        deleteMongoAndRedisInfo(CACHE_KEY);
    }

    @Test
    public void shouldReturnNoContentWhenRedisHasNoCacheAndMongoDBHasNoData() {
        final var uri = "/v1/deliveries/models";
        final var statusList = List.of("ACTIVE");
        final var cacheKey = String.format(CACHE_KEY.concat("%s"), statusList);

        webTestClient
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        Mockito.verify(deliveryRepository, Mockito.times(1)).findByStatusIn(statusList);

        deleteMongoAndRedisInfo(cacheKey);
    }

    private void deleteMongoAndRedisInfo(String cacheKey) {
        deliveryRepository.deleteAll().subscribe();
        reactiveRedisTemplate.opsForSet().delete(cacheKey).subscribe();
    }

}
