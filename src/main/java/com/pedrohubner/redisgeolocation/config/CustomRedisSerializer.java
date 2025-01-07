package com.pedrohubner.redisgeolocation.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
public class CustomRedisSerializer implements RedisSerializer<Object> {
    private final Jackson2JsonRedisSerializer<Object> jsonSerializer;
    private final StringRedisSerializer stringSerializer;

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o instanceof String) {
            return stringSerializer.serialize((String) o);
        } else {
            return jsonSerializer.serialize(o);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            return jsonSerializer.deserialize(bytes);
        } catch (SerializationException e) {
            return stringSerializer.deserialize(bytes);
        }
    }
}
