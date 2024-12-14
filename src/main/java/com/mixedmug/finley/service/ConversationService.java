package com.mixedmug.finley.service;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private final ConversationManager conversationManager;
    private final MessageProcessor messageProcessor;

    public ConversationService(ConversationManager conversationManager, MessageProcessor messageProcessor) {
        this.conversationManager = conversationManager;
        this.messageProcessor = messageProcessor;
    }

    public Mono<ConversationResponse> handleUserQuery(UserQuery userQuery) {
        if (!userQuery.isValid())
            return Mono.error(new IllegalArgumentException("UserQuery is not valid"));

        return conversationManager.getConversation(userQuery.getEmail())
                .flatMap(conversation -> messageProcessor.processUserMessage(conversation, userQuery))
                .onErrorResume(e -> handleProcessingError(userQuery.getEmail(), e));
    }

    private Mono<ConversationResponse> handleProcessingError(String email, Throwable e) {
        logger.error("Error processing chat for user [{}]: {}", email, e.getMessage(), e);
        return Mono.error(new RuntimeException("Failed to process chat", e));
    }

    public Mono<Conversation> getConversation(String userId) {
        return conversationManager.getConversation(userId)
                .flatMap(Mono::just)
                .onErrorResume(e -> handleRetrievalError(userId, e));
    }

    private Mono<Conversation> handleRetrievalError(String userId, Throwable e) {
        logger.error("Error processing chat for user [{}]: {}", userId, e.getMessage(), e);
        return Mono.error(new RuntimeException("Failed to process chat", e));
    }

    public Mono<Void> resetConversation(String userId) {
        return conversationManager.resetConversation(userId)
                .doOnSuccess(unused -> logger.info("Reset conversation for userId: {}", userId))
                .doOnError(e -> logger.error("Error resetting conversation for userId: {}", userId, e));
    }
}