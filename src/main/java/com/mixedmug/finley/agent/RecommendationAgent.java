package com.mixedmug.finley.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.*;
import com.mixedmug.finley.service.AgentResponseService;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class RecommendationAgent extends AAgent {

    private final AgentResponseService agentResponseService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecommendationAgent(AnthropicService anthropicService, AgentResponseService agentResponseService, ObjectMapper objectMapper) throws IOException {
        super(anthropicService, "recommendation_prompt.md");
        this.agentResponseService = agentResponseService;
        this.objectMapper = objectMapper;
    }

    public Mono<ConversationResponse> generateResponse(final UserIntent userIntent, final ProductConfiguration productConfiguration, String lastMessage, Long conversationId) {
        String prompt = promptTemplate
                .replace("{{PRODUCT_CATEGORY}}", productConfiguration.getProductCategory())
                .replace("{{PRODUCT_DESCRIPTION}}", productConfiguration.getProductDescription())
                .replace("{{INFORMATION_GATHERING_LIST}}", productConfiguration.getInformationGatheringList())
                .replace("{{MOOD}}", userIntent.getMood().toString())
                .replace("{{CONVERSATION_CONTEXT}}", userIntent.getContext())
                .replace("{{USER_PROMPT}}", lastMessage);

        return getCompletion(prompt)
                .map(jsonText -> {
                    try {
                        return objectMapper.readValue(jsonText, AgentResponse.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse JSON into AgentResponse", e);
                    }
                })
                .flatMap(response -> {
                    response.setConversationId(conversationId);
                    return agentResponseService.save(response)
                            .map(agentResponse -> new ConversationResponse(agentResponse.getMessage()));
                });
    }
}