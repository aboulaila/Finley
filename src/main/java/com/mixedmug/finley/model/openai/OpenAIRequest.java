package com.mixedmug.finley.model.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Request payload for OpenAI completion API")
public class OpenAIRequest {

    @Schema(description = "Model name to use for completion", example = "gpt-4o-mini")
    private String model;

    @Schema(description = "A list of messages comprising the conversation so far.")
    private OpenAIMessage[] messages;

    @Schema(description = "An upper bound for the number of tokens that can be generated for a completion, including visible output tokens and reasoning tokens.", example = "50")
    private int max_completion_tokens;

    @Schema(description = "Sampling temperature", example = "0.7")
    private double temperature;

    @Schema(description = "Number of completions to generate", example = "1")
    private int n;

    @Data
    @AllArgsConstructor
    @Schema(description = "Message content")
    public static class OpenAIMessage {

        @Schema(description = "The role of the messages author", example = "user")
        private String role;

        @Schema(description = "The contents of the system message.", example = "Hello!")
        private String content;
    }
}