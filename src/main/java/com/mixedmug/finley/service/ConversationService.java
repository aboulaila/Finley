package com.mixedmug.finley.service;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private final ConversationRepository conversationRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Mono<Conversation> getConversation(String userId) {
        return conversationRepository.findByUserId(userId)
                .switchIfEmpty(Mono.defer(() -> {
                    Conversation newConversation = new Conversation(userId);
                    logger.info("Creating new conversation for userId: {}", userId);
                    return conversationRepository.save(newConversation);
                }))
                .doOnSuccess(conv -> logger.debug("Retrieved conversation for userId: {}", userId))
                .doOnError(e -> logger.error("Error retrieving conversation for userId: {}", userId, e));
    }

    public Mono<Conversation> saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation)
                .doOnSuccess(conv -> logger.debug("Saved conversation for userId: {}", conv.getUserId()))
                .doOnError(e -> logger.error("Error saving conversation for userId: {}", conversation.getUserId(), e));
    }

    public Mono<Void> resetConversation(String userId) {
        return conversationRepository.deleteByUserId(userId)
                .doOnSuccess(unused -> logger.info("Reset conversation for userId: {}", userId))
                .doOnError(e -> logger.error("Error resetting conversation for userId: {}", userId, e));
    }
}