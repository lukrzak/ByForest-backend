package com.lukrzak.ByForest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ByForestApplication {

	public static final String BASE_URL = "/api/v1";

	public static void main(String[] args) {
		SpringApplication.run(ByForestApplication.class, args);
	}

}
