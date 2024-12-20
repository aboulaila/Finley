package com.mixedmug.finley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user_intents")
public class UserIntent {
    @Id
    private Long id; // Primary key
    private Long conversationId;
    private String userId;
    private Intents intent;
    private Moods mood;
    private String context;
    private String summary;

    public enum Moods {
        NEUTRAL("NEUTRAL"),
        SATISFIED("SATISFIED"),
        UPSET("UPSET");

        private final String value;

        Moods(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static Moods fromValue(String value) {
            for (Moods reason : Moods.values()) {
                if (reason.value.equalsIgnoreCase(value)) {
                    return reason;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }

    public enum Intents {
        BUYING_NEW_PRODUCT("BUYING_NEW_PRODUCT"),
        OTHER_REQUEST("OTHER_REQUEST");

        private final String value;

        Intents(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static Intents fromValue(String value) {
            for (Intents reason : Intents.values()) {
                if (reason.value.equalsIgnoreCase(value)) {
                    return reason;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }
}
