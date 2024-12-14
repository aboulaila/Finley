package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserIntent;
import com.mixedmug.finley.service.UserIntentService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OrchestratorAgent {
    private final IntentClassifierAgent intentClassifierAgent;
    private final ModeratorAgent moderatorAgent;
    private final RecommendationAgent recommendationAgent;
    private final OffTopicAgent offTopicAgent;
    private final UserIntentService userIntentService;

    public OrchestratorAgent(IntentClassifierAgent intentClassifierAgent, ModeratorAgent moderatorAgent, RecommendationAgent recommendationAgent, OffTopicAgent offTopicAgent, UserIntentService userIntentService) {
        this.intentClassifierAgent = intentClassifierAgent;
        this.moderatorAgent = moderatorAgent;
        this.recommendationAgent = recommendationAgent;
        this.offTopicAgent = offTopicAgent;
        this.userIntentService = userIntentService;
    }

    public Mono<ConversationResponse> processInput(Conversation conversation, String lastMessage) {
        return moderatorAgent.isPromptAllowed(lastMessage)
                .flatMap(decision -> {
                    if (Boolean.FALSE.equals(decision)) {
                        return Mono.just(new ConversationResponse("I wont reply to this"));
                    }

                    return intentClassifierAgent.parseUserIntent(conversation, lastMessage)
                        .flatMap(userIntent -> this.userIntentService.saveIntent(userIntent)
                            .flatMap(newIntent -> {
                                if (newIntent.getIntent().equals(UserIntent.Intents.BUYING_NEW_PRODUCT)) {
                                    return recommendationAgent.generateResponse(newIntent, lastMessage);
                                } else {
                                    return offTopicAgent.generateResponse(lastMessage);
                                }
                            }));
                });
    }
}
