package com.mixedmug.finley.service;

import com.mixedmug.finley.model.TopicRelevance;
import com.mixedmug.finley.repository.TopicRelevanceRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Service
public class TopicRelevanceService {

    private static final Logger log = LoggerFactory.getLogger(TopicRelevanceService.class);
    private final TopicRelevanceRepository topicRelevanceRepository;

    public TopicRelevanceService(TopicRelevanceRepository topicRelevanceRepository) {
        this.topicRelevanceRepository = topicRelevanceRepository;
    }

    public Mono<TopicRelevance> saveTopicRelevance(TopicRelevance topicRelevance) {
        return topicRelevanceRepository.save(topicRelevance)
                .doOnSuccess(savedTopic -> log.info("Saved Topic Relevance: {}", savedTopic))
                .doOnError(error -> log.error("Error saving topic relevance: {}", error.getMessage()));
    }

    public Flux<TopicRelevance> getTopicRelevanceByConversationId(Long conversationId) {
        return topicRelevanceRepository.findByConversationId(conversationId)
                .doOnComplete(() -> log.info("Retrieved topic relevances for conversationId: {}", conversationId))
                .doOnError(err -> log.error("Error retrieving topic relevances for conversationId: {}, error: {}", conversationId, err));
    }

    public Flux<TopicRelevance> getAllTopicRelevance() {
        return topicRelevanceRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all topic relevances"))
                .doOnError(err -> log.error("Error retrieving all topic relevances: {}", String.valueOf(err)));
    }
}
