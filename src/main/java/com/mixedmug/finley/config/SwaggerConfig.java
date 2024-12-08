package com.mixedmug.finley.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Finley OpenAI API")
                        .description("API documentation for Finley")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Finley Wiki Documentation")
                        .url("https://finley.wiki/docs"));
    }
}