package com.mixedmug.finley.controller;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.service.ConversationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public Flux<Conversation> getAllConversations() {
        return conversationService.getAllConversations();
    }

    @GetMapping("/{email}")
    public Mono<Conversation> getConversation(@PathVariable String email) {
        return conversationService.getConversation(email);
    }

    @PostMapping("/reset")
    public Mono<Void> resetConversation(@RequestParam String email) {
        return conversationService.resetConversation(email);
    }
}
