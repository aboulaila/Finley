package com.mixedmug.finley.service.openai;

import com.mixedmug.finley.exception.OpenAIServiceException;
import com.mixedmug.finley.model.openai.OpenAIRequest;
import com.mixedmug.finley.model.openai.OpenAIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAIServiceTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestBodySpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private OpenAIService openAIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCompletion_Success() {
        OpenAIRequest request = OpenAIRequest.builder()
                .model("gpt-4o-mini")
                .messages(new OpenAIRequest.OpenAIMessage[]{new OpenAIRequest.OpenAIMessage("user", "Hello!")})
                .max_completion_tokens(50)
                .temperature(0.7)
                .n(1)
                .build();

        OpenAIResponse expectedResponse = new OpenAIResponse();
        // Initialize expectedResponse fields as needed

        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.bodyValue(any(OpenAIRequest.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(OpenAIResponse.class)).thenReturn(Mono.just(expectedResponse));

        OpenAIResponse actualResponse = openAIService.getCompletion(request).block();

        assert actualResponse == expectedResponse;
        verify(webClientMock, times(1)).post();
    }

    @Test
    void testGetCompletion_Error() {
        OpenAIRequest request = OpenAIRequest.builder()
                .model("gpt-4o-mini")
                .messages(new OpenAIRequest.OpenAIMessage[]{new OpenAIRequest.OpenAIMessage("user", "Hello!")})
                .max_completion_tokens(50)
                .temperature(0.7)
                .n(1)
                .build();

        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.bodyValue(any(OpenAIRequest.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(OpenAIResponse.class)).thenReturn(Mono.error(new WebClientResponseException("Bad Request", 400, "Bad Request", null, null, null)));

        try {
            openAIService.getCompletion(request);
        } catch (OpenAIServiceException e) {
            assert e.getMessage().contains("Error calling OpenAI API");
        }

        verify(webClientMock, times(1)).post();
    }
}