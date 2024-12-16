package com.mixedmug.finley.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.*;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class IntentClassifierAgent extends AAgent {
    private final ObjectMapper objectMapper;

    @Autowired
    public IntentClassifierAgent(AnthropicService anthropicService, ObjectMapper objectMapper) throws IOException {
        super(anthropicService, "intent_prompt.md");
        this.objectMapper = objectMapper;
    }

    public Mono<UserIntent> parseUserIntent(final Conversation conversation, final String input) {
        String intentsString = Arrays.stream(UserIntent.Intents.values())
                .map(UserIntent.Intents::getValue)
                .collect(Collectors.joining(", "));

        String moodsString = Arrays.stream(UserIntent.Moods.values())
                .map(UserIntent.Moods::getValue)
                .collect(Collectors.joining(", "));

        String conversationString = conversation.getMessages().stream()
                .map(message -> String.format("role: %s, message: %s", message.getRole(), message.getContent()))
                .collect(Collectors.joining(", "));

        String prompt = promptTemplate
                .replace("{{INTENT_LIST}}", intentsString)
                .replace("{{MOOD_LIST}}", moodsString)
                .replace("{{CONVERSATION}}", conversationString)
                .replace("{{USER_PROMPT}}", input);

        return getCompletion(prompt)
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
