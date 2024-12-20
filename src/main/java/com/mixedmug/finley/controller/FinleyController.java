package com.mixedmug.finley.controller;

import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import com.mixedmug.finley.service.ConversationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finley")
@SecurityRequirement(name = "oauth2")
public class FinleyController {
    private final ConversationService conversationService;

    @Autowired
    public FinleyController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/chat")
    public Mono<ConversationResponse> processChatRequest(@RequestBody UserQuery userQuery) {
        if (userQuery.isValid()) {
            return Mono.error(new IllegalArgumentException("Invalid UserQuery: email and message fields must not be blank or null."));
        }
        return conversationService.handleUserQuery(userQuery);
    }
}
