package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserIntent;
import com.mixedmug.finley.service.ProductConfigurationService;
import com.mixedmug.finley.service.UserIntentService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class OrchestratorAgent {
    public static final long BIKES_CATEGORY_ID = 1L;
    public static final long COMPUTERS_CATEGORY_ID = 2L;

    private static final String PROMPT_NOT_ALLOWED_MESSAGE = "I won't reply to this";
    private final IntentClassifierAgent intentClassifierAgent;
    private final ModeratorAgent moderatorAgent;
    private final RecommendationAgent recommendationAgent;
    private final OffTopicAgent offTopicAgent;
    private final UserIntentService userIntentService;
    private final ProductConfigurationService productConfigurationService;

    public OrchestratorAgent(IntentClassifierAgent intentClassifierAgent, ModeratorAgent moderatorAgent,
                             RecommendationAgent recommendationAgent, OffTopicAgent offTopicAgent,
                             UserIntentService userIntentService, ProductConfigurationService productConfigurationService) {
        this.intentClassifierAgent = intentClassifierAgent;
        this.moderatorAgent = moderatorAgent;
        this.recommendationAgent = recommendationAgent;
        this.offTopicAgent = offTopicAgent;
        this.userIntentService = userIntentService;
        this.productConfigurationService = productConfigurationService;
    }

    public Mono<ConversationResponse> processInput(Conversation conversation, String lastMessage) {
        return moderatorAgent.isPromptAllowed(lastMessage)
                .flatMap(isAllowed -> Boolean.TRUE.equals(isAllowed)
                        ? processAllowedConversation(conversation, lastMessage)
                        : Mono.just(new ConversationResponse(PROMPT_NOT_ALLOWED_MESSAGE)));
    }

    private Mono<ConversationResponse> processAllowedConversation(Conversation conversation, String lastMessage) {
        return intentClassifierAgent.parseUserIntent(conversation, lastMessage)
                .flatMap(userIntent -> userIntentService.saveIntent(userIntent)
                        .flatMap(savedIntent -> processUserIntent(savedIntent, lastMessage, conversation.getId())));
    }

    private Mono<ConversationResponse> processUserIntent(UserIntent userIntent, String lastMessage, Long conversationId) {
        if (Objects.requireNonNull(userIntent.getIntent()) == UserIntent.Intents.BUYING_NEW_PRODUCT) {
            return handleBuyingNewProductIntent(userIntent, lastMessage, conversationId);
        }
        return offTopicAgent.generateResponse(conversationId, userIntent.getContext(), lastMessage, UserIntent.Intents.BUYING_NEW_PRODUCT.getValue());
    }

    private Mono<ConversationResponse> handleBuyingNewProductIntent(UserIntent userIntent, String lastMessage, Long conversationId) {
        return productConfigurationService.getById(COMPUTERS_CATEGORY_ID)
                .flatMap(productConfiguration -> recommendationAgent.generateResponse(userIntent, productConfiguration, lastMessage, conversationId));
    }
}
