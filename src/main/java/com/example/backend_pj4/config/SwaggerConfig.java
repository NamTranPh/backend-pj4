package com.example.backend_pj4.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

//Dang sua o day de hien thi view Swagger và các api
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Movie API Documentation",
        version = "1.0.0"
    ),
    servers = {
        // @Server(url = "http://localhost:3004/api", description = "Local Development Server"),
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class SwaggerConfig {
}
