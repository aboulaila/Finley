package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.Message;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class AAgent {
    private final AnthropicService anthropicService;
    private final String promptTemplateFile;
    protected final String promptTemplate;

    protected AAgent(AnthropicService anthropicService, String promptTemplateFile) throws IOException {
        this.anthropicService = anthropicService;
        this.promptTemplateFile = promptTemplateFile;
        this.promptTemplate = loadPromptTemplate();
    }

    protected String loadPromptTemplate() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/prompts/" + promptTemplateFile);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: /prompts/intent_prompt.md");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public Mono<String> getCompletion(final String prompt) {
        var messages = new ArrayList<Message>();
        messages.add(new Message("user", prompt));

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0.5)
                .messages(messages.toArray(new Message[0]))
                .build();

        return anthropicService.getCompletion(request)
                .map(response -> response.getChoices().get(0).getText());
    }
}
