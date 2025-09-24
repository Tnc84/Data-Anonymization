package com.tnc.Data.Anonymization.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for the Data Anonymization API.
 * Provides comprehensive API documentation and Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Data Anonymization API")
                        .version("1.0.0")
                        .description("A comprehensive REST API for anonymizing sensitive data using various strategies including pseudonymization, masking, and redaction.")
                        .contact(new Contact()
                                .name("TNC Development Team")
                                .email("dev@tnc.com")
                                .url("https://tnc.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development server"),
                        new Server()
                                .url("https://api.tnc.com")
                                .description("Production server")
                ));
    }
}
