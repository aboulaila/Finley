package com.mixedmug.finley.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table("conversations")
@Data
public class Conversation {
    @Id
    private String userId;
    //todo: to reduce the overhead, summarize the chat when messages reach a certain threshold
    private List<AIRequest.AIMessage> messages = new ArrayList<>();

    public Conversation(String userId) {
        this.userId = userId;
    }

    public void addMessage(String role, String message) {
        this.messages.add(new AIRequest.AIMessage(role, message));
    }
}