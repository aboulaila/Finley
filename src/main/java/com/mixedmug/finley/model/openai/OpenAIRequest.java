package com.mixedmug.finley.model.openai;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.Message;
import lombok.Data;

@Data
public class OpenAIRequest {
    private String model;
    private Message[] messages;
    private int max_completion_tokens;
    private double temperature;
    private int n;

    public OpenAIRequest(AIRequest aiRequest) {
        this.model = aiRequest.getModel();
        this.messages = aiRequest.getMessages();
        this.max_completion_tokens = aiRequest.getMaxTokens();
        this.temperature = aiRequest.getTemperature();
        this.n = aiRequest.getN();
    }
}