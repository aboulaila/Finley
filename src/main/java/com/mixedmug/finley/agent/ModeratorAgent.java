package com.mixedmug.finley.agent;

import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class ModeratorAgent extends AAgent {
    @Autowired
    public ModeratorAgent(AnthropicService anthropicService) throws IOException {
        super(anthropicService, "moderator_prompt.md");
    }

    public Mono<Boolean> isPromptAllowed(final String input) {
        String prompt = promptTemplate
                .replace("{{USER_INPUT}}", input);

        return getCompletion(prompt)
                .map(response -> response.contains("(N)"));
    }
}
