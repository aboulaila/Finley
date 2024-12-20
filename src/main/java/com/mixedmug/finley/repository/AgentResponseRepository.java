package com.mixedmug.finley.repository;

import com.mixedmug.finley.model.AgentResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AgentResponseRepository extends ReactiveCrudRepository<AgentResponse, Long> {
    Flux<AgentResponse> findByConversationId(Long conversationId);
}