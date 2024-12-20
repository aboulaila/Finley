package com.mixedmug.finley.model;

import lombok.Data;

@Data
public class MessageModeration {
    private String moderation_result;
    private String explanation;
}
