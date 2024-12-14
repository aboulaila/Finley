package com.mixedmug.finley.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery {
    private String email;
    private String message;

    public boolean isValid() {
        return email != null && message != null && !email.isBlank() && !message.isBlank();
    }
}
