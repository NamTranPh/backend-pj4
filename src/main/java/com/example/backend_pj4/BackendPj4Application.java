package com.example.backend_pj4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendPj4Application {

	public static void main(String[] args) {
		SpringApplication.run(BackendPj4Application.class, args);
	}

}
