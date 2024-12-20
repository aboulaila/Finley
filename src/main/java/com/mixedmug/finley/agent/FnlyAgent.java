package com.mixedmug.finley.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.*;
import com.mixedmug.finley.service.AgentResponseService;
import com.mixedmug.finley.service.TopicRelevanceService;
import com.mixedmug.finley.service.UserIntentService;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class FnlyAgent extends AAgent {

    private static final String DEFAULT_INTENT = UserIntent.Intents.BUYING_NEW_PRODUCT.getValue();

    private final ObjectMapper objectMapper;
    private final UserIntentService userIntentService;
    private final TopicRelevanceService topicRelevanceService;
    private final AgentResponseService agentResponseService;

    protected FnlyAgent(AnthropicService anthropicService,
                        ObjectMapper objectMapper,
                        UserIntentService userIntentService,
                        TopicRelevanceService topicRelevanceService,
                        AgentResponseService agentResponseService) throws IOException {
        super(anthropicService, "fnly_prompt.md");
        this.objectMapper = objectMapper;
        this.userIntentService = userIntentService;
        this.topicRelevanceService = topicRelevanceService;
        this.agentResponseService = agentResponseService;
    }

    public Mono<ConversationResponse> handleUserInput(final Conversation conversation,
                                                      final String input,
                                                      final ProductConfiguration productConfiguration) {
        String prompt = createPrompt(conversation, input, productConfiguration);

        return getCompletion(prompt)
                .flatMap(this::parseResponse)
                .flatMap(response -> saveResponseDetails(response, conversation)
                        .thenReturn(response))
                .map(response -> new ConversationResponse(response.getProduct_recommendation().getMessage()));
    }

    private String createPrompt(Conversation conversation, String input, ProductConfiguration productConfiguration) {
        String intentList = Arrays.stream(UserIntent.Intents.values())
                .map(UserIntent.Intents::getValue)
                .collect(Collectors.joining(", "));
        String moodList = Arrays.stream(UserIntent.Moods.values())
                .map(UserIntent.Moods::getValue)
                .collect(Collectors.joining(", "));
        String formattedConversation = conversation.getMessages().stream()
                .map(message -> String.format("role: %s, message: %s", message.getRole(), message.getContent()))
                .collect(Collectors.joining(", "));

        return promptTemplate
                .replace("{{CONVERSATION}}", formattedConversation)
                .replace("{{INTENT_LIST}}", intentList)
                .replace("{{MOOD_LIST}}", moodList)
                .replace("{{INTENT}}", DEFAULT_INTENT)
                .replace("{{PRODUCT_CATEGORY}}", productConfiguration.getProductCategory())
                .replace("{{PRODUCT_DESCRIPTION}}", productConfiguration.getProductDescription())
                .replace("{{INFORMATION_GATHERING_LIST}}", productConfiguration.getInformationGatheringList())
                .replace("{{USER_INPUT}}", input);
    }

    private Mono<FnlyResponse> parseResponse(String jsonText) {
        return Mono.fromCallable(() -> objectMapper.readValue(jsonText, FnlyResponse.class))
                .onErrorMap(IOException.class, e -> new RuntimeException("Failed to parse JSON into FnlyResponse", e));
    }

    private Mono<Void> saveResponseDetails(FnlyResponse response, Conversation conversation) {
        Long conversationId = conversation.getId();

        // Populate conversation IDs
        var userIntent = response.getIntent_classification();
        userIntent.setConversationId(conversationId);
        userIntent.setUserId(conversation.getUserId());

        var topicRelevance = response.getTopic_relevance();
        topicRelevance.setConversationId(conversationId);

        var productRecommendation = response.getProduct_recommendation();
        productRecommendation.setConversationId(conversationId);

        // Save all entities reactively and ensure chaining
        return userIntentService.saveIntent(userIntent)
                .then(topicRelevanceService.saveTopicRelevance(topicRelevance))
                .then(agentResponseService.save(productRecommendation))
                .then();
    }
}
