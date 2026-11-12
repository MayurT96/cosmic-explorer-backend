package com.cosmicexplorer.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger UI at /swagger-ui.html. Use the Authorize button with the token from /api/auth/login. */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Cosmic Explorer AI API", version = "1.0",
        description = "Planets, exoplanets, missions, quiz, accounts, AI assistant and NASA data."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
