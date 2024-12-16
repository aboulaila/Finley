package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.ConversationResponse;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class OffTopicAgent extends AAgent {

    @Autowired
    public OffTopicAgent(AnthropicService anthropicService) throws IOException {
        super(anthropicService, "offtopic_prompt.md");
    }

    public Mono<ConversationResponse> generateResponse(final String input) {
        String prompt = promptTemplate
                .replace("{{USER_INPUT}}", input);

        return getCompletion(prompt)
                .map(ConversationResponse::new);
    }
}
