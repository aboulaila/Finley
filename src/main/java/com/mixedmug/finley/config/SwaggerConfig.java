package com.mixedmug.finley.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth",
                        tokenUrl = "https://oauth2.googleapis.com/token",
                        scopes = {
                                @OAuthScope(name = "openid", description = "OpenID Connect scope"),
                                @OAuthScope(name = "profile", description = "User profile information"),
                                @OAuthScope(name = "email", description = "User email address")
                        }
                )
        )
)
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        var servers = new ArrayList<Server>();
        servers.add(new Server().url("/").description("default"));
        return new OpenAPI()
                .servers(servers)
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("oauth2"))
                .info(new Info().title("Finley OpenAI API")
                        .description("API documentation for Finley")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Finley Wiki Documentation")
                        .url("https://finley.wiki/docs"));
    }
}