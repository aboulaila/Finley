package com.mixedmug.finley.service;

import com.mixedmug.finley.dto.AgentResponseDTO;
import com.mixedmug.finley.model.AgentResponse;
import com.mixedmug.finley.repository.AgentResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
public class AgentResponseService {

    private final AgentResponseRepository agentResponseRepository;

    @Autowired
    public AgentResponseService(AgentResponseRepository agentResponseRepository) {
        this.agentResponseRepository = agentResponseRepository;
    }

    public Flux<AgentResponseDTO> getAll() {
        return agentResponseRepository.findAll()
                .map(this::mapToDTO);
    }

    public Flux<AgentResponseDTO> getByConversationId(Long conversationId) {
        return agentResponseRepository.findByConversationId(conversationId)
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new NoSuchElementException("AgentResponse not found for conversationId: " + conversationId)));
    }

    public Mono<AgentResponse> save(AgentResponse agentResponse) {
        return agentResponseRepository.save(agentResponse);
    }

    public Mono<AgentResponseDTO> updateConfiguration(Long id, AgentResponseDTO agentResponseDTO) {
        return agentResponseRepository.findById(id)
                .flatMap(existingConfig -> {
                    existingConfig.setMessage(agentResponseDTO.getMessage());
                    existingConfig.setThoughtProcess(agentResponseDTO.getThoughtProcess());
                    existingConfig.setConversationId(agentResponseDTO.getConversationId());
                    return agentResponseRepository.save(existingConfig);
                })
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new NoSuchElementException("AgentResponse not found with id: " + id)));
    }

    private AgentResponseDTO mapToDTO(AgentResponse response) {
        return new AgentResponseDTO(
                response.getId(),
                response.getConversationId(),
                response.getThoughtProcess(),
                response.getMessage()
        );
    }

    private AgentResponse mapToEntity(AgentResponseDTO dto) {
        return new AgentResponse(
                dto.getId(),
                dto.getConversationId(),
                dto.getThoughtProcess(),
                dto.getMessage()
        );
    }
}