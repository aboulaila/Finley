package com.mixedmug.finley.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponseDTO {
    @Id
    private Long id;
    private Long conversationId;
    private String thoughtProcess;
    private String message;
}
