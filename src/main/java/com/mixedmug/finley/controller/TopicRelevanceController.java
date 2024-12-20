package com.mixedmug.finley.controller;

import com.mixedmug.finley.model.TopicRelevance;
import com.mixedmug.finley.service.TopicRelevanceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/topic_relevance")
public class TopicRelevanceController {
    private final TopicRelevanceService topicRelevanceService;

    public TopicRelevanceController(TopicRelevanceService topicRelevanceService) {
        this.topicRelevanceService = topicRelevanceService;
    }

    @GetMapping
    public Flux<TopicRelevance> getAll() {
        return topicRelevanceService.getAllTopicRelevance();
    }

    @GetMapping("/{conversationId}")
    public Flux<TopicRelevance> getConversation(@PathVariable Long conversationId) {
        return topicRelevanceService.getTopicRelevanceByConversationId(conversationId);
    }
}
