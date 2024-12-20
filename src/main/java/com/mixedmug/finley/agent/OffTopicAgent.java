package com.mixedmug.finley.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.TopicRelevance;
import com.mixedmug.finley.service.TopicRelevanceService;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class OffTopicAgent extends AAgent {
    private final ObjectMapper objectMapper;
    private final TopicRelevanceService topicRelevanceService;

    @Autowired
    public OffTopicAgent(AnthropicService anthropicService, ObjectMapper objectMapper, TopicRelevanceService topicRelevanceService) throws IOException {
        super(anthropicService, "offtopic_prompt.md");
        this.objectMapper = objectMapper;
        this.topicRelevanceService = topicRelevanceService;
    }

    public Mono<ConversationResponse> generateResponse(Long conversationId, final String conversationContext, final String lastMessage, final String userIntent) {
        String prompt = buildPrompt(conversationContext, userIntent, lastMessage);
        return getCompletion(prompt)
                .flatMap(this::parseMessageEvaluation)
                .flatMap(topicRelevance -> processMessageEvaluation(topicRelevance, conversationId)
                        .thenReturn(topicRelevance.getMessage()))
                .map(ConversationResponse::new)
                .onErrorMap(JsonProcessingException.class, e -> new RuntimeException("Failed to parse JSON into MessageEvaluation", e))
                .onErrorMap(IllegalArgumentException.class, e -> new RuntimeException("User input validation failed", e));

    }


    private String buildPrompt(String conversationContext, String userIntent, String lastMessage) {
        return promptTemplate
                .replace("{{CONVERSATION}}", conversationContext)
                .replace("{{INTENT}}", userIntent)
                .replace("{{USER_MESSAGE}}", lastMessage);
    }

    private Mono<TopicRelevance> parseMessageEvaluation(String jsonText) {
        try {
            return Mono.just(objectMapper.readValue(jsonText, TopicRelevance.class));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private Mono<Void> processMessageEvaluation(TopicRelevance topicRelevance, Long conversationId) {
        topicRelevance.setConversationId(conversationId);
        return topicRelevanceService.saveTopicRelevance(topicRelevance)
                .then(Mono.defer(() -> {
//                    if (!topicRelevance.isOn_topic()) {
//                        return Mono.error(new IllegalArgumentException("User is spamming"));
//                    }
                    return Mono.empty();
                }));
    }
}
