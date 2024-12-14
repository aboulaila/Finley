package com.mixedmug.finley.service;

import org.springframework.stereotype.Service;
import com.mixedmug.finley.model.UserIntent;
import com.mixedmug.finley.repository.UserIntentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserIntentService {

    private final UserIntentRepository userIntentRepository;

    public UserIntentService(UserIntentRepository userIntentRepository) {
        this.userIntentRepository = userIntentRepository;
    }

    public Mono<UserIntent> saveIntent(UserIntent userIntent) {
        return userIntentRepository.save(userIntent);
    }

    public Flux<UserIntent> getIntentsByUserId(String userId) {
        return userIntentRepository.findByUserId(userId);
    }

    public Flux<UserIntent> getIntentsByConversationId(Long conversationId) {
        return userIntentRepository.findByConversationId(conversationId);
    }

    public Flux<UserIntent> getAllIntents() {
        return userIntentRepository.findAll();
    }

    public Mono<UserIntent> findIntentById(Long id) {
        return userIntentRepository.findById(id);
    }

    public Mono<Void> deleteIntentById(Long id) {
        return userIntentRepository.deleteById(id);
    }
}