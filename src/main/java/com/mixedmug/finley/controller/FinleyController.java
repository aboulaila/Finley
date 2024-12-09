package com.mixedmug.finley.controller;

import com.mixedmug.finley.agent.RecommendationAgent;
import com.mixedmug.finley.model.AIResponse;
import com.mixedmug.finley.model.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api/finley")
public class FinleyController {

    private static final Logger logger = LoggerFactory.getLogger(FinleyController.class);
    private final RecommendationAgent recommendationAgent;

    @Autowired
    public FinleyController(RecommendationAgent recommendationAgent) {
        this.recommendationAgent = recommendationAgent;
    }

    @PostMapping("/recommend")
    public Mono<AIResponse> getRecommendation(@RequestBody UserQuery userQuery) throws IOException {
        return recommendationAgent.generateResponse(userQuery)
                .doOnSuccess(response -> logger.info("Successfully processed /recommend request: {}", response))
                .doOnError(e -> logger.error("Error processing /recommend request: {}", e.getMessage(), e));
    }
}