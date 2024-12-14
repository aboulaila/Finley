package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.Message;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Component
public class ModeratorAgent {
    private final AnthropicService anthropicService;
    private final String promptTemplate;

    @Autowired
    public ModeratorAgent(AnthropicService anthropicService) throws IOException {
        this.anthropicService = anthropicService;
        this.promptTemplate = loadPromptTemplate();
    }

    private String loadPromptTemplate() throws IOException {
        Path path = Paths.get("src", "main", "resources", "prompts", "moderator_prompt.md");
        return Files.readString(path);
    }

    public Mono<Boolean> isPromptAllowed(final String input) {
        var messages = new ArrayList<Message>();
        messages.add(new Message("user", promptTemplate));
        messages.add(new Message("user", input));

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0.5)
                .messages(messages.toArray(new Message[0]))
                .build();

        return anthropicService.getCompletion(request)
                .map(response -> response.getChoices().get(0).getText())
                .map(response -> response.contains("(N)"));
    }
}
