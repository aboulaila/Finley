package com.mixedmug.finley.service;

import com.mixedmug.finley.model.openai.OpenAIRequest;
import com.mixedmug.finley.model.openai.OpenAIResponse;
import reactor.core.publisher.Mono;

public interface AIService {
    Mono<OpenAIResponse> getCompletion(OpenAIRequest request);
}
