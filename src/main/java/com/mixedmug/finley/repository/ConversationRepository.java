package com.mixedmug.finley.repository;

import com.mixedmug.finley.model.Conversation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, Long> {
    Mono<Conversation> findByUserId(String userId);
    Mono<Void> deleteByUserId(String userId);
}