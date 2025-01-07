package com.pedrohubner.redisgeolocation.config;

import com.pedrohubner.redisgeolocation.delivery.model.DeliveryModelResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> template(final ReactiveRedisConnectionFactory factory) {
        final var jackson2JsonRedisSerializer = new CustomRedisSerializer(new Jackson2JsonRedisSerializer<>(Object.class), new StringRedisSerializer());


        final var context = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(jackson2JsonRedisSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jackson2JsonRedisSerializer)
                .build();


        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, DeliveryModelResponse> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        final var keySerializer = new StringRedisSerializer();
        final var valueSerializer = new Jackson2JsonRedisSerializer<>(DeliveryModelResponse.class);

        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.<String, DeliveryModelResponse>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build());
    }

    @Bean
    public ReactiveRedisTemplate<String, String> deliveryIdRedisTemplate(ReactiveRedisConnectionFactory factory) {
        final var keySerializer = new StringRedisSerializer();
        final var valueSerializer = new StringRedisSerializer();

        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.<String, String>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build());
    }

}
