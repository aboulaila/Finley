package com.mixedmug.finley.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.*;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class IntentClassifierAgent {
    private final AnthropicService anthropicService;
    private final String promptTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public IntentClassifierAgent(AnthropicService anthropicService, ObjectMapper objectMapper) throws IOException {
        this.anthropicService = anthropicService;
        this.objectMapper = objectMapper;
        this.promptTemplate = loadPromptTemplate();
    }

    private String loadPromptTemplate() throws IOException {
        Path path = Paths.get("src", "main", "resources", "prompts", "intent_prompt.md");
        return Files.readString(path);
    }

    public Mono<UserIntent> parseUserIntent(final Conversation conversation, final String input) {
        var messages = new ArrayList<Message>();
        String intentsString = Arrays.stream(UserIntent.Intents.values())
                .map(UserIntent.Intents::getValue)
                .collect(Collectors.joining(", "));

        String moodsString = Arrays.stream(UserIntent.Moods.values())
                .map(UserIntent.Moods::getValue)
                .collect(Collectors.joining(", "));

        String conversationString = conversation.getMessages().stream()
                .map(message -> String.format("role: %s, message: %s", message.getRole(), message.getContent()))
                .collect(Collectors.joining(", "));

        String prompt = promptTemplate.replace("{{INTENT_LIST}}", intentsString)
                .replace("{{MOOD_LIST}}", moodsString)
                .replace("{{CONVERSATION}}", conversationString)
                .replace("{{USER_PROMPT}}", input);
        messages.add(new Message("user", prompt));

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0.5)
                .messages(messages.toArray(new Message[0]))
                .build();

        return anthropicService.getCompletion(request)
                .map(response -> response.getChoices().get(0).getText())
                .map(jsonText -> {
                    try {
                        return objectMapper.readValue(jsonText, UserIntent.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse JSON into UserIntent", e);
                    }
                })
                .map(userIntent -> {
                    userIntent.setUserId(conversation.getUserId());
                    userIntent.setConversationId(conversation.getId());
                    return userIntent;
                });
    }
}
