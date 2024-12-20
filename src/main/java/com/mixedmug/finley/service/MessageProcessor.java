package com.mixedmug.finley.service;

import com.mixedmug.finley.agent.FnlyAgent;
import com.mixedmug.finley.agent.OrchestratorAgent;
import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.mixedmug.finley.agent.OrchestratorAgent.COMPUTERS_CATEGORY_ID;

@Service
public class MessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    //    private final OrchestratorAgent orchestratorAgent;
    private final FnlyAgent fnlyAgent;
    private final ConversationManager conversationManager;
    private final ProductConfigurationService productConfigurationService;

    public MessageProcessor(FnlyAgent fnlyAgent, ConversationManager conversationManager, ProductConfigurationService productConfigurationService) {
        this.fnlyAgent = fnlyAgent;
        this.conversationManager = conversationManager;
        this.productConfigurationService = productConfigurationService;
    }

    public Mono<ConversationResponse> processUserMessage(Conversation conversation, UserQuery userQuery) {
        addUserMessage(conversation, userQuery);
        return productConfigurationService.getById(COMPUTERS_CATEGORY_ID)
                .flatMap(category -> fnlyAgent.handleUserInput(conversation, userQuery.getMessage(), category)
                .flatMap(response -> processAgentResponse(conversation, userQuery.getEmail(), response)));
    }

    private Mono<ConversationResponse> processAgentResponse(Conversation conversation, String email, ConversationResponse response) {
        addAssistantMessage(conversation, email, response.getMessage());

        return conversationManager.saveConversation(conversation)
                .then(Mono.just(response));
    }

    private void addUserMessage(Conversation conversation, UserQuery userQuery) {
        conversation.addMessage("user", userQuery.getMessage());
        logger.info("User [{}] sent message: {}", userQuery.getEmail(), userQuery.getMessage());
    }

    private void addAssistantMessage(Conversation conversation, String email, String message) {
        conversation.addMessage("assistant", message);
        logger.info("Responded to user [{}]: {}", email, message);
    }
}
