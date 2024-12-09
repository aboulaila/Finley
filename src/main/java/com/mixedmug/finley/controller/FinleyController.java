package com.mixedmug.finley.controller;

import com.mixedmug.finley.agent.RecommendationAgent;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import com.mixedmug.finley.security.FinleyUserDetails;
import com.mixedmug.finley.service.ConversationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finley")
@SecurityRequirement(name = "oauth2")
public class FinleyController {

    private static final Logger logger = LoggerFactory.getLogger(FinleyController.class);
    private final RecommendationAgent recommendationAgent;
    private final ConversationService conversationService;

    @Autowired
    public FinleyController(RecommendationAgent recommendationAgent, ConversationService conversationService) {
        this.recommendationAgent = recommendationAgent;
        this.conversationService = conversationService;
    }

    @PostMapping("/chat")
    public Mono<ConversationResponse> chat(@RequestBody UserQuery userQuery) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.error(new SecurityException("User is not authenticated"));
        }

        FinleyUserDetails userDetails = (FinleyUserDetails) authentication.getPrincipal();
        String email = userDetails.getEmail();
        String message = userQuery.getMessage();

        return conversationService.getConversation(email)
                .flatMap(conversation -> {
                    conversation.addMessage("user", message);
                    logger.info("User [{}] sent message: {}", email, message);

                    return recommendationAgent.generateResponse(conversation)
                            .flatMap(response -> {
                                conversation.addMessage("assistant", response.getMessage());
                                logger.info("Responded to user [{}]: {}", email, response.getMessage());

                                return conversationService.saveConversation(conversation)
                                        .then(Mono.just(new ConversationResponse(response.getMessage())));
                            });
                })
                .onErrorResume(e -> {
                    logger.error("Error processing chat for user [{}]: {}", email, e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to process chat", e));
                });
    }

    // Optional: Endpoint to reset or delete a conversation
    @PostMapping("/chat/reset")
    public Mono<ConversationResponse> resetConversation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // Handle unauthenticated access
            return Mono.error(new SecurityException("User is not authenticated"));
        }

        FinleyUserDetails userDetails = (FinleyUserDetails) authentication.getPrincipal();
        String userId = userDetails.getEmail();

        return conversationService.resetConversation(userId)
                .then(Mono.just(new ConversationResponse("Conversation reset successfully.")))
                .doOnSuccess(response -> logger.info("Conversation reset for user [{}]", userId))
                .onErrorResume(e -> {
                    logger.error("Error resetting conversation for user [{}]: {}", userId, e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to reset conversation", e));
                });
    }
}