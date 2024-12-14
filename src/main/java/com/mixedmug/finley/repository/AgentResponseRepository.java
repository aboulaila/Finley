package com.mixedmug.finley.repository;

import com.mixedmug.finley.model.AgentResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AgentResponseRepository extends ReactiveCrudRepository<AgentResponse, Long> {
    Mono<AgentResponse> findByConversationId(Long conversationId);
}