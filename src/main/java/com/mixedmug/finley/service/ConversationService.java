package com.mixedmug.finley.service;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private static final String PROCESSING_ERROR_MESSAGE = "Error processing chat for user";
    private static final String RETRIEVAL_ERROR_MESSAGE = "Error retrieving chat for user";
    private static final String RESET_ERROR_MESSAGE = "Error resetting conversation for userId";

    private final ConversationManager conversationManager;
    private final MessageProcessor messageProcessor;

    public ConversationService(ConversationManager conversationManager, MessageProcessor messageProcessor) {
        this.conversationManager = conversationManager;
        this.messageProcessor = messageProcessor;
    }

    public Mono<ConversationResponse> handleUserQuery(UserQuery userQuery) {
        return conversationManager.getConversation(userQuery.getEmail())
                .flatMap(conversation -> messageProcessor.processUserMessage(conversation, userQuery))
                .onErrorResume(e -> logAndReturnError(
                        PROCESSING_ERROR_MESSAGE, userQuery.getEmail(), e, new RuntimeException("Failed to process chat", e)
                ));
    }

    public Mono<Conversation> getConversation(String userId) {
        return conversationManager.getConversation(userId)
                .onErrorResume(e -> logAndReturnError(
                        RETRIEVAL_ERROR_MESSAGE, userId, e, new RuntimeException("Failed to retrieve chat", e)
                ));
    }

    public Flux<Conversation> getAllConversations() {
        return conversationManager.getConversations()
                .doOnError(e -> logger.error("Error retrieving all conversations: {}", e.getMessage(), e));
    }

    public Mono<Void> resetConversation(String userId) {
        return conversationManager.resetConversation(userId)
                .doOnSuccess(unused -> logger.info("Reset conversation successfully for userId: {}", userId))
                .doOnError(e -> logger.error("{} [{}]: {}", RESET_ERROR_MESSAGE, userId, e.getMessage(), e));
    }

    // Private Helpers
    private <T> Mono<T> logAndReturnError(String specificErrorMessage, String identifier, Throwable e, RuntimeException resultException) {
        logger.error("{} [{}]: {}", specificErrorMessage, identifier, e.getMessage(), e);
        return Mono.error(resultException);
    }
}
