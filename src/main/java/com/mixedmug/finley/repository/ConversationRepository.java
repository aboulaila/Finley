package com.mixedmug.finley.repository;

import com.mixedmug.finley.model.Conversation;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, Long> {
    Mono<Conversation> findByUserId(String userId);
    @Modifying
    @Query("UPDATE conversations SET user_id = :newUserId WHERE user_id = :currentUserId")
    Mono<Void> updateUserId(@Param("currentUserId") String currentUserId, @Param("newUserId") String newUserId);
}