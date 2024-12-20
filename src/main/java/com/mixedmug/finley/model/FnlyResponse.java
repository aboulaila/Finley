package com.mixedmug.finley.model;

import lombok.Data;

@Data
public class FnlyResponse {
    private MessageModeration moderation;
    private UserIntent intent_classification;
    private TopicRelevance topic_relevance;
    private AgentResponse product_recommendation;
}
