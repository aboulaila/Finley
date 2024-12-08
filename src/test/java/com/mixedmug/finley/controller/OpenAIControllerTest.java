package com.mixedmug.finley.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.exception.OpenAIServiceException;
import com.mixedmug.finley.model.openai.OpenAIRequest;
import com.mixedmug.finley.model.openai.OpenAIResponse;
import com.mixedmug.finley.service.openai.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(OpenAIController.class)
class OpenAIControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OpenAIService openAIService;

    @Autowired
    private ObjectMapper objectMapper;

    private OpenAIRequest request;
    private OpenAIResponse response;

    @BeforeEach
    void setUp() {
        request = OpenAIRequest.builder()
                .model("gpt-4o-mini")
                .messages(new OpenAIRequest.OpenAIMessage[]{new OpenAIRequest.OpenAIMessage("user", "Hello!")})
                .max_completion_tokens(50)
                .temperature(0.7)
                .n(1)
                .build();

        response = new OpenAIResponse();
    }

    @Test
    void testGetCompletionAsync_Success() throws Exception {
        when(openAIService.getCompletion(any(OpenAIRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/openai/completion")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OpenAIResponse.class)
                .isEqualTo(response);

        verify(openAIService, times(1)).getCompletion(any(OpenAIRequest.class));
    }

    @Test
    void testGetCompletionAsync_Error() throws Exception {
        when(openAIService.getCompletion(any(OpenAIRequest.class)))
                .thenReturn(Mono.error(new OpenAIServiceException("API Error")));

        webTestClient.post()
                .uri("/api/openai/completion")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("An error occurred while processing your request: API Error");

        verify(openAIService, times(1)).getCompletion(any(OpenAIRequest.class));
    }
}