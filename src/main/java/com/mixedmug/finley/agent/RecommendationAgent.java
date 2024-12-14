package com.mixedmug.finley.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.*;
import com.mixedmug.finley.service.AgentResponseService;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

@Component
public class RecommendationAgent {

    private final AnthropicService anthropicService;
    private final AgentResponseService agentResponseService;
    private final String promptTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecommendationAgent(AnthropicService anthropicService, AgentResponseService agentResponseService, ObjectMapper objectMapper) throws IOException {
        this.anthropicService = anthropicService;
        this.agentResponseService = agentResponseService;
        this.objectMapper = objectMapper;
        this.promptTemplate = loadPromptTemplate();
    }

    private String loadPromptTemplate() throws IOException {
        Path path = Paths.get("src", "main", "resources", "prompts", "recommendation_prompt.md");
        return Files.readString(path);
    }

    public Mono<ConversationResponse> generateResponse(final UserIntent userIntent, final ProductConfiguration productConfiguration, String lastMessage, Long conversationId) {
        var messages = new ArrayList<Message>();

        String prompt = promptTemplate
                .replace("{{PRODUCT_CATEGORY}}", productConfiguration.getProductCategory())
                .replace("{{PRODUCT_DESCRIPTION}}", productConfiguration.getProductDescription())
                .replace("{{INFORMATION_GATHERING_LIST}}", productConfiguration.getInformationGatheringList())
                .replace("{{MOOD}}", userIntent.getMood().toString())
                .replace("{{CONVERSATION_CONTEXT}}", userIntent.getContext())
                .replace("{{USER_PROMPT}}", lastMessage);
        messages.add(new Message("user", prompt));

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0)
                .messages(messages.toArray(new Message[0]))
                .build();

        return anthropicService.getCompletion(request)
                .map(response -> response.getChoices().get(0).getText())
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