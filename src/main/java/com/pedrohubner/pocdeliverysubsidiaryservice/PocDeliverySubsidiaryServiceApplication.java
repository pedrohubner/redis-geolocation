package com.pedrohubner.pocdeliverysubsidiaryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheConfig;

@CacheConfig
@SpringBootApplication
public class PocDeliverySubsidiaryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocDeliverySubsidiaryServiceApplication.class, args);
	}

}
