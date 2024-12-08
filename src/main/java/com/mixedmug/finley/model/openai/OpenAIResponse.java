package com.mixedmug.finley.model.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Response from OpenAI completion API")
public class OpenAIResponse {

    @Schema(description = "Unique ID for the completion", example = "cmpl-5u5T0N8x6KX6S0tY0")
    private String id;

    @Schema(description = "Object type", example = "text_completion")
    private String object;

    @Schema(description = "Timestamp of creation", example = "1610078133")
    private long created;

    @Schema(description = "Model used for completion", example = "text-davinci-003")
    private String model;

    @Schema(description = "List of completion choices")
    private List<Choice> choices;

    @Schema(description = "Usage statistics")
    private Usage usage;

    @Data
    @Schema(description = "Individual choice in the response")
    public static class Choice {
        @Schema(description = "Generated text", example = " there was a brave knight named Sir Lancelot...")
        private String text;

        @Schema(description = "Index of the choice", example = "0")
        private int index;

        @Schema(description = "Reason for finishing generation", example = "stop")
        private String finish_reason;
    }

    @Data
    @Schema(description = "Usage statistics for the request")
    public static class Usage {
        @Schema(description = "Number of tokens in the prompt", example = "9")
        private int prompt_tokens;

        @Schema(description = "Number of tokens in the completion", example = "50")
        private int completion_tokens;

        @Schema(description = "Total tokens used", example = "59")
        private int total_tokens;
    }
}