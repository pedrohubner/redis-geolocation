package com.pedrohubner.redisgeolocation.delivery;

import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface DeliveryRepository extends ReactiveMongoRepository<DeliveryModelResponse, String> {

    Flux<DeliveryModelResponse> findByStatusIn(List<String> status);

}
