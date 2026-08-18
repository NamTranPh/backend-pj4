package com.example.backend_pj4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class BackendPj4Application {

	public static void main(String[] args) {
		SpringApplication.run(BackendPj4Application.class, args);
	}

}
