package com.mixedmug.finley.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("topic_relevance")
public class TopicRelevance {
    @Id
    private Long id;
    private Long conversationId;
    private String message;
    private String analysis;
    private boolean on_topic;
}
