package com.mixedmug.finley.controller;

import org.springframework.web.bind.annotation.*;
import com.mixedmug.finley.model.UserIntent;
import com.mixedmug.finley.service.UserIntentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/intents")
public class UserIntentController {

    private final UserIntentService userIntentService;

    public UserIntentController(UserIntentService userIntentService) {
        this.userIntentService = userIntentService;
    }

    @GetMapping("/user/{userId}")
    public Flux<UserIntent> getIntentsByUserId(@PathVariable String userId) {
        return userIntentService.getIntentsByUserId(userId);
    }

    @GetMapping("/conversation/{conversationId}")
    public Flux<UserIntent> getIntentsByConversationId(@PathVariable Long conversationId) {
        return userIntentService.getIntentsByConversationId(conversationId);
    }

    @GetMapping
    public Flux<UserIntent> getAllIntents() {
        return userIntentService.getAllIntents();
    }

    @GetMapping("/{id}")
    public Mono<UserIntent> getIntentById(@PathVariable Long id) {
        return userIntentService.findIntentById(id);
    }
}