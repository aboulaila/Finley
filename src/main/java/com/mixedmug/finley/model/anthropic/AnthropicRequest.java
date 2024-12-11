package com.mixedmug.finley.model.anthropic;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.Message;
import lombok.Data;

@Data
public class AnthropicRequest {
    private String model;
    private Message[] messages;
    private int max_tokens;
    private double temperature;

    public AnthropicRequest(AIRequest aiRequest) {
        this.model = aiRequest.getModel();
        this.messages = aiRequest.getMessages();
        this.max_tokens = aiRequest.getMaxTokens();
        this.temperature = aiRequest.getTemperature();
    }
}