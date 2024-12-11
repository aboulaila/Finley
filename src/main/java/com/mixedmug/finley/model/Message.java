package com.mixedmug.finley.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Message content")
public class Message {

    @Schema(description = "The role of the messages author", example = "user")
    private String role;

    @Schema(description = "The contents of the system message.", example = "Hello!")
    private String content;
}
