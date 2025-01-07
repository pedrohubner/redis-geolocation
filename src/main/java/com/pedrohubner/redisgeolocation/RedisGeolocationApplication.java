package com.pedrohubner.redisgeolocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheConfig;

@CacheConfig
@SpringBootApplication
public class RedisGeolocationApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisGeolocationApplication.class, args);
	}

}
