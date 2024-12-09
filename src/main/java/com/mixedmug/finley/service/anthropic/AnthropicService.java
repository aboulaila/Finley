package com.mixedmug.finley.service.anthropic;

import com.mixedmug.finley.exception.OpenAIServiceException;
import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.AIResponse;
import com.mixedmug.finley.model.anthropic.AnthropicRequest;
import com.mixedmug.finley.model.anthropic.AnthropicResponse;
import com.mixedmug.finley.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AnthropicService implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AnthropicService.class);
    private final WebClient webClient;

    @Autowired
    public AnthropicService(WebClient anthropicWebClient) {
        this.webClient = anthropicWebClient;
    }

    @Override
//    @Cacheable(value = "openAIResponseCache", key = "#request.prompt")
    public Mono<AIResponse> getCompletion(AIRequest request) {
        var anthropicRequest = new AnthropicRequest(request);
        logger.info("Received async request for Anthropic completion: {}", request);
        return webClient.post()
                .bodyValue(anthropicRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.error("Error response from Anthropic API: {}", errorBody);
                            return Mono.error(new OpenAIServiceException("Error calling Anthropic API: " + errorBody));
                        })
                )
                .bodyToMono(AnthropicResponse.class)
                .map(AnthropicResponse::toAIResponse)
                .doOnNext(response -> logger.info("Received async response from Anthropic API: {}", response))
                .doOnError(e -> logger.error("Exception in async Anthropic API call: {}", e.getMessage(), e));
    }
}