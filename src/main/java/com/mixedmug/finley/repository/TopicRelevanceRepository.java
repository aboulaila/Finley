package com.mixedmug.finley.repository;

import com.mixedmug.finley.model.TopicRelevance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TopicRelevanceRepository extends ReactiveCrudRepository<TopicRelevance, Long> {
    Flux<TopicRelevance> findByConversationId(Long conversationId);
}
