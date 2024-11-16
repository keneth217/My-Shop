package com.laptop.Laptop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "com.laptop.Laptop")
@EntityScan(basePackages = "com.laptop.Laptop.entity")
@EnableCaching
public class LaptopApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptopApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
