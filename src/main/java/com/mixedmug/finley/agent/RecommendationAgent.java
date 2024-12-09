package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
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
    private final String promptTemplate;

    @Autowired
    public RecommendationAgent(AnthropicService anthropicService) throws IOException {
        this.anthropicService = anthropicService;
        this.promptTemplate = loadPromptTemplate();
    }

    private String loadPromptTemplate() throws IOException {
        Path path = Paths.get("src", "main", "resources", "prompts", "recommendation_prompt.md");
        return Files.readString(path);
    }

    public Mono<ConversationResponse> generateResponse(final Conversation conversation) {
        var messages = new ArrayList<AIRequest.AIMessage>();
        messages.add(new AIRequest.AIMessage("user", promptTemplate));
        messages.addAll(conversation.getMessages());

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0)
                .messages(messages.toArray(new AIRequest.AIMessage[0]))
                .build();

        return anthropicService.getCompletion(request)
                .map(response -> new ConversationResponse(response.getChoices().get(0).getText()));
    }
}