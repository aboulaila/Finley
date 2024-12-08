package com.mixedmug.finley.service.openai;

import com.mixedmug.finley.exception.OpenAIServiceException;
import com.mixedmug.finley.model.openai.OpenAIRequest;
import com.mixedmug.finley.model.openai.OpenAIResponse;
import com.mixedmug.finley.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIService implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private final WebClient webClient;

    @Autowired
    public OpenAIService(WebClient openAIWebClient) {
        this.webClient = openAIWebClient;
    }

    @Override
//    @Cacheable(value = "openAIResponseCache", key = "#request.prompt")
    public Mono<OpenAIResponse> getCompletion(OpenAIRequest request) {
        logger.info("Received async request for OpenAI completion: {}", request);
        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.error("Error response from OpenAI API: {}", errorBody);
                            return Mono.error(new OpenAIServiceException("Error calling OpenAI API: " + errorBody));
                        })
                )
                .bodyToMono(OpenAIResponse.class)
                .doOnNext(response -> logger.info("Received async response from OpenAI API: {}", response))
                .doOnError(e -> logger.error("Exception in async OpenAI API call: {}", e.getMessage(), e));
    }
}