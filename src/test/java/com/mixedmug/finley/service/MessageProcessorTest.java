package com.mixedmug.finley.service;

import com.mixedmug.finley.agent.RecommendationAgent;
import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.model.UserQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageProcessorTest {

    @Mock
    private RecommendationAgent recommendationAgent;

    @Mock
    private ConversationManager conversationManager;

    @InjectMocks
    private MessageProcessor messageProcessor;

    @Captor
    private ArgumentCaptor<Conversation> conversationCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processUserMessage_ShouldAddUserMessage_GenerateResponse_AddAssistantMessage_SaveConversation() {
        // Arrange
        String userEmail = "user@example.com";
        String userMessage = "Hello, how are you?";
        String assistantMessage = "I'm good, thank you! How can I assist you today?";

        UserQuery userQuery = new UserQuery();
        userQuery.setEmail(userEmail);
        userQuery.setMessage(userMessage);

        Conversation conversation = new Conversation(userEmail);

        ConversationResponse agentResponse = new ConversationResponse(assistantMessage);

        when(recommendationAgent.generateResponse(any(Conversation.class), lastMessage))
                .thenReturn(Mono.just(agentResponse));

        when(conversationManager.saveConversation(any(Conversation.class)))
                .thenReturn(Mono.just(conversation));

        // Act
        Mono<ConversationResponse> result = messageProcessor.processUserMessage(conversation, userQuery);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getMessage().equals(assistantMessage))
                .verifyComplete();

        // Verify that the user's message was added
        assert conversation.getMessages().stream()
                .anyMatch(msg -> msg.getRole().equals("user") && msg.getContent().equals(userMessage));

        // Verify that the assistant's message was added
        assert conversation.getMessages().stream()
                .anyMatch(msg -> msg.getRole().equals("assistant") && msg.getContent().equals(assistantMessage));

        // Verify interactions
        verify(recommendationAgent, times(1)).generateResponse(conversation, lastMessage);
        verify(conversationManager, times(1)).saveConversation(conversationCaptor.capture());

        // Capture the conversation passed to saveConversation and verify its state
        Conversation savedConversation = conversationCaptor.getValue();
        assert savedConversation.getMessages().size() == 2;
        assert savedConversation.getMessages().get(0).getRole().equals("user");
        assert savedConversation.getMessages().get(0).getContent().equals(userMessage);
        assert savedConversation.getMessages().get(1).getRole().equals("assistant");
        assert savedConversation.getMessages().get(1).getContent().equals(assistantMessage);
    }

    @Test
    void processUserMessage_ShouldHandleAgentResponseError() {
        // Arrange
        String userEmail = "user@example.com";
        String userMessage = "Hello, how are you?";

        UserQuery userQuery = new UserQuery();
        userQuery.setEmail(userEmail);
        userQuery.setMessage(userMessage);

        Conversation conversation = new Conversation(userEmail);

        RuntimeException agentException = new RuntimeException("Agent failure");

        when(recommendationAgent.generateResponse(any(Conversation.class), lastMessage))
                .thenReturn(Mono.error(agentException));

        // Act
        Mono<ConversationResponse> result = messageProcessor.processUserMessage(conversation, userQuery);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Agent failure"))
                .verify();

        // Verify that only the user's message was added
        assert conversation.getMessages().stream()
                .anyMatch(msg -> msg.getRole().equals("user") && msg.getContent().equals(userMessage));

        // Verify that the assistant's message was not added
        assert conversation.getMessages().stream()
                .noneMatch(msg -> msg.getRole().equals("assistant"));

        // Verify interactions
        verify(recommendationAgent, times(1)).generateResponse(conversation, lastMessage);
        verify(conversationManager, times(0)).saveConversation(any(Conversation.class));
    }

    @Test
    void processUserMessage_ShouldHandleSaveConversationError() {
        // Arrange
        String userEmail = "user@example.com";
        String userMessage = "Hello, how are you?";
        String assistantMessage = "I'm good, thank you! How can I assist you today?";

        UserQuery userQuery = new UserQuery();
        userQuery.setEmail(userEmail);
        userQuery.setMessage(userMessage);

        Conversation conversation = new Conversation(userEmail);

        ConversationResponse agentResponse = new ConversationResponse(assistantMessage);

        when(recommendationAgent.generateResponse(any(Conversation.class), lastMessage))
                .thenReturn(Mono.just(agentResponse));

        RuntimeException saveException = new RuntimeException("Save failure");

        when(conversationManager.saveConversation(any(Conversation.class)))
                .thenReturn(Mono.error(saveException));

        // Act
        Mono<ConversationResponse> result = messageProcessor.processUserMessage(conversation, userQuery);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failure"))
                .verify();

        // Verify that both user and assistant messages were added
        assert conversation.getMessages().stream()
                .anyMatch(msg -> msg.getRole().equals("user") && msg.getContent().equals(userMessage));

        assert conversation.getMessages().stream()
                .anyMatch(msg -> msg.getRole().equals("assistant") && msg.getContent().equals(assistantMessage));

        // Verify interactions
        verify(recommendationAgent, times(1)).generateResponse(conversation, lastMessage);
        verify(conversationManager, times(1)).saveConversation(conversation);
    }
}