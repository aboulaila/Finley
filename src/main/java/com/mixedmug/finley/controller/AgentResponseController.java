package com.mixedmug.finley.controller;

import com.mixedmug.finley.dto.AgentResponseDTO;
import com.mixedmug.finley.service.AgentResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agent-responses")
public class AgentResponseController {
    private final AgentResponseService agentResponseService;

    @Autowired
    public AgentResponseController(AgentResponseService agentResponseService) {
        this.agentResponseService = agentResponseService;
    }

    @GetMapping
    public Flux<AgentResponseDTO> getAllConfigurations() {
        return agentResponseService.getAll();
    }

    @GetMapping("/{conversationId}")
    public Mono<AgentResponseDTO> getByConversationId(@PathVariable Long conversationId) {
        return agentResponseService.getByConversationId(conversationId);
    }
}