package com.pedrohubner.pocdeliverysubsidiaryservice.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisServerConfig {

    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
//    private static final String REDIS_DOCKER_IMAGE = "redis:latest";

    @Container
    static GenericContainer genericContainer = new GenericContainer(REDIS_DOCKER_IMAGE).withExposedPorts(6379);

    @PostConstruct
    public void setup() {
        genericContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", genericContainer::getHost);
        registry.add("spring.data.redis.port", genericContainer::getFirstMappedPort);
    }

    @PreDestroy
    public void teardown() {
        genericContainer.stop();
    }

}
