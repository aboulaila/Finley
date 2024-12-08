package com.mixedmug.finley.model;

import lombok.Data;

@Data
public class AIRequest {
    private String input;
    private int temperature;
    private long maxTokens;
}
