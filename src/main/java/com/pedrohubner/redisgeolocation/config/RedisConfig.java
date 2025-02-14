package com.pedrohubner.redisgeolocation.config;

import com.pedrohubner.redisgeolocation.deliveryarea.model.response.DeliveryAreaResponse;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
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
        final var jackson2JsonRedisSerializer = new CustomRedisSerializer(
                new Jackson2JsonRedisSerializer<>(Object.class), new StringRedisSerializer()
        );

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
    public ReactiveRedisTemplate<String, DeliveryAreaResponse> deliveryAreaTemplate(
            ReactiveRedisConnectionFactory factory
    ) {
        final var keySerializer = new StringRedisSerializer();
        final var valueSerializer = new Jackson2JsonRedisSerializer<>(DeliveryAreaResponse.class);

        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.<String, DeliveryAreaResponse>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build());
    }

    @Bean
    public ReactiveRedisTemplate<String, Long> geolocationTemplate(
            ReactiveRedisConnectionFactory factory
    ) {
        final var keySerializer = new StringRedisSerializer();
        final var valueSerializer = new Jackson2JsonRedisSerializer<>(Long.class);

        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext.<String, Long>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build()
        );
    }

    @Bean
    public ReactiveRedisTemplate<String, SubsidiaryResponse> subsidiaryTemplate(
            ReactiveRedisConnectionFactory factory
    ) {
        final var keySerializer = new StringRedisSerializer();
        final var valueSerializer = new Jackson2JsonRedisSerializer<>(SubsidiaryResponse.class);

        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext.<String, SubsidiaryResponse>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build()
        );
    }
}
