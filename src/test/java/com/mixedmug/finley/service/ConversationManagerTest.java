package com.mixedmug.finley.service;

import com.mixedmug.finley.model.Conversation;
import com.mixedmug.finley.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConversationManagerTest {

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private ConversationManager conversationManager;

    @Captor
    private ArgumentCaptor<Conversation> conversationCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getConversation_WhenConversationExists_ShouldReturnExistingConversation() {
        // Arrange
        String userId = "user@example.com";
        Conversation existingConversation = new Conversation(userId);
        existingConversation.addMessage("user", "Hello!");

        when(conversationRepository.findByUserId(userId))
                .thenReturn(Mono.just(existingConversation));

        // Act
        Mono<Conversation> result = conversationManager.getConversation(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(existingConversation)
                .verifyComplete();

        verify(conversationRepository, times(1)).findByUserId(userId);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void getConversation_WhenConversationDoesNotExist_ShouldCreateAndSaveNewConversation() {
        // Arrange
        String userId = "newuser@example.com";
        Conversation newConversation = new Conversation(userId);

        when(conversationRepository.findByUserId(userId))
                .thenReturn(Mono.empty());

        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(Mono.just(newConversation));

        // Act
        Mono<Conversation> result = conversationManager.getConversation(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(newConversation)
                .verifyComplete();

        verify(conversationRepository, times(1)).findByUserId(userId);
        verify(conversationRepository, times(1)).save(conversationCaptor.capture());

        Conversation savedConversation = conversationCaptor.getValue();
        assert savedConversation.getUserId().equals(userId);
        assert savedConversation.getMessages().isEmpty();
    }

    @Test
    void getConversation_ShouldHandleRepositoryError() {
        // Arrange
        String userId = "erroruser@example.com";
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(conversationRepository.findByUserId(userId))
                .thenReturn(Mono.error(repositoryException));

        // Act
        Mono<Conversation> result = conversationManager.getConversation(userId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(conversationRepository, times(1)).findByUserId(userId);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void saveConversation_ShouldSaveAndReturnConversation() {
        // Arrange
        String userId = "saveuser@example.com";
        Conversation conversationToSave = new Conversation(userId);
        conversationToSave.addMessage("user", "Hi there!");

        when(conversationRepository.save(conversationToSave))
                .thenReturn(Mono.just(conversationToSave));

        // Act
        Mono<Conversation> result = conversationManager.saveConversation(conversationToSave);

        // Assert
        StepVerifier.create(result)
                .expectNext(conversationToSave)
                .verifyComplete();

        verify(conversationRepository, times(1)).save(conversationToSave);
    }

    @Test
    void saveConversation_ShouldHandleSaveError() {
        // Arrange
        String userId = "saveerror@example.com";
        Conversation conversationToSave = new Conversation(userId);
        conversationToSave.addMessage("user", "Error message");

        RuntimeException saveException = new RuntimeException("Save failed");

        when(conversationRepository.save(conversationToSave))
                .thenReturn(Mono.error(saveException));

        // Act
        Mono<Conversation> result = conversationManager.saveConversation(conversationToSave);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(conversationRepository, times(1)).save(conversationToSave);
    }

    @Test
    void resetConversation_ShouldDeleteConversation() {
        // Arrange
        String userId = "resetuser@example.com";

        when(conversationRepository.deleteByUserId(userId))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = conversationManager.resetConversation(userId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(conversationRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void resetConversation_ShouldHandleDeleteError() {
        // Arrange
        String userId = "reseterror@example.com";
        RuntimeException deleteException = new RuntimeException("Delete failed");

        when(conversationRepository.deleteByUserId(userId))
                .thenReturn(Mono.error(deleteException));

        // Act
        Mono<Void> result = conversationManager.resetConversation(userId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Delete failed"))
                .verify();

        verify(conversationRepository, times(1)).deleteByUserId(userId);
    }
}