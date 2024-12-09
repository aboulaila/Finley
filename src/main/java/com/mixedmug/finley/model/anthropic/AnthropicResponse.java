package com.mixedmug.finley.model.anthropic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mixedmug.finley.model.AIResponse;
import lombok.Data;

import java.util.List;
import java.util.stream.IntStream;

@Data
public class AnthropicResponse {
    private String id;
    private String type;
    private String role;
    private List<Content> content;
    private String model;
    private StopReason stop_reason;
    private String stop_sequence;
    private Usage usage;

    public AIResponse toAIResponse() {
        return AIResponse.builder()
                .id(id)
                .object(type)
                .model(model)
                .choices(IntStream.range(0, content.size()).mapToObj(index -> new AIResponse.Choice(content.get(index).text, index, stop_reason.toString())).toList())
                .usage(new AIResponse.Usage(usage.input_tokens, usage.output_tokens, usage.input_tokens + usage.output_tokens))
                .build();
    }

    @Data
    public static class Content {
        private String type;
        private String text;
    }

    @Data
    public static class Usage {
        private int input_tokens;
        private int cache_creation_input_tokens;
        private int cache_read_input_tokens;
        private int output_tokens;
    }

    public enum StopReason {
        END_TURN("end_turn"),
        MAX_TOKENS("max_tokens"),
        STOP_SEQUENCE("stop_sequence"),
        TOOL_USE("tool_use");

        private final String value;

        StopReason(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static StopReason fromValue(String value) {
            for (StopReason reason : StopReason.values()) {
                if (reason.value.equalsIgnoreCase(value)) {
                    return reason;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }
}