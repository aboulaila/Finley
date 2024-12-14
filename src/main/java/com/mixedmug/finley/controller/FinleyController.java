package com.mixedmug.finley.controller;

import com.mixedmug.finley.model.Conversation;
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
    public Mono<ConversationResponse> chat(@RequestBody UserQuery userQuery) {
        return conversationService.handleUserQuery(userQuery)
                .flatMap(Mono::just)
                .onErrorResume(Mono::error);
    }

    @GetMapping("/conversation")
    public Mono<Conversation> conversation(@RequestParam String email) {
        return conversationService.getConversation(email)
                .flatMap(Mono::just)
                .onErrorResume(Mono::error);
    }

    @PostMapping("/chat/reset")
    public Mono<Void> resetConversation(@RequestParam String email) {
        return conversationService.resetConversation(email)
                .flatMap(Mono::just)
                .onErrorResume(Mono::error);
    }
}