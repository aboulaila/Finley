package com.mixedmug.finley.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.mixedmug.finley.model.UserIntent;
import reactor.core.publisher.Flux;

public interface UserIntentRepository extends ReactiveCrudRepository<UserIntent, Long> {
    Flux<UserIntent> findByUserId(String userId);
    Flux<UserIntent> findByConversationId(Long conversationId);
}