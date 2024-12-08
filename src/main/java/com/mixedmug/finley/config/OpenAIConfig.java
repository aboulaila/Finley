package com.mixedmug.finley.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public WebClient openAIWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(openaiApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
}
