package com.mixedmug.finley.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("agent_response")
public class AgentResponse {

    @Id
    private Long id;
    private Long conversationId;
    private String thoughtProcess;
    private String message;
}