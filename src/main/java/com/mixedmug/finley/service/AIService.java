package com.mixedmug.finley.service;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.AIResponse;
import reactor.core.publisher.Mono;

public interface AIService {
    Mono<AIResponse> getCompletion(AIRequest request);
}
