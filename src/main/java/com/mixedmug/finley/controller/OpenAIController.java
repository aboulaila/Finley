package com.mixedmug.finley.controller;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.AIResponse;
import com.mixedmug.finley.model.openai.OpenAIRequest;
import com.mixedmug.finley.service.openai.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIController.class);
    private final OpenAIService openAIService;

    @Autowired
    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/completion")
    public Mono<AIResponse> getCompletion(@RequestBody OpenAIRequest request) {
        logger.info("Received POST /completion request: {}", request);

        var aiRequest = AIRequest.builder()
                .model(request.getModel())
                .maxTokens(request.getMax_completion_tokens())
                .temperature(request.getTemperature())
                .messages(request.getMessages())
                .n(request.getN())
                .build();

        return openAIService.getCompletion(aiRequest)
                .doOnSuccess(response -> logger.info("Successfully processed /completion request: {}", response))
                .doOnError(e -> logger.error("Error processing /completion request: {}", e.getMessage(), e));
    }
}