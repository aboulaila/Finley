package com.mixedmug.finley.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Request payload for completion API")
public class AIRequest {

    @Schema(description = "Model name to use for completion", example = "gpt-4o-mini")
    private String model;

    @Schema(description = "A list of messages comprising the conversation so far.")
    private Message[] messages;

    @Schema(description = "An upper bound for the number of tokens that can be generated for a completion, including visible output tokens and reasoning tokens.", example = "50")
    private int maxTokens;

    @Schema(description = "Sampling temperature", example = "0.7")
    private double temperature;

    @Schema(description = "Number of completions to generate", example = "1")
    private int n;
}