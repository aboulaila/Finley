package com.mixedmug.finley.agent;

import com.mixedmug.finley.model.AIRequest;
import com.mixedmug.finley.model.AIResponse;
import com.mixedmug.finley.model.UserQuery;
import com.mixedmug.finley.service.anthropic.AnthropicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;

@Component
public class RecommendationAgent {

    private final AnthropicService anthropicService;
    private final String promptTemplate;

    @Autowired
    public RecommendationAgent(AnthropicService anthropicService) throws IOException {
        this.anthropicService = anthropicService;
        this.promptTemplate = loadPromptTemplate();
    }

    private String loadPromptTemplate() throws IOException {
        Path path = Paths.get("src", "main", "resources", "prompts", "recommendation_prompt.md");
        return Files.readString(path);
    }

    public Mono<AIResponse> generateResponse(UserQuery userQuery) {
        String taskDescription = """
                Finley est un assistant virtuel conçu pour aider les résidents français à choisir le vélo, vélo électrique, trottinette ou pièce de vélo parfait pour leurs besoins. Notre objectif principal est de simplifier le processus de sélection en comprenant les exigences spécifiques, les préférences et les contraintes de chaque utilisateur.
                "Notre public cible comprend des cyclistes de tous niveaux, des débutants aux experts, âgés de 18 à 65 ans, vivant dans les zones urbaines et suburbaines de France. Finley doit être capable de répondre à une variété de besoins, qu'il s'agisse de trouver un vélo pour les déplacements quotidiens, les loisirs du week-end ou les compétitions sportives.
                "L'objectif de Finley est d'augmenter les ventes de vélos et d'accessoires de 20% en fournissant des recommandations personnalisées et en améliorant l'expérience d'achat en ligne. Nous visons également à réduire le taux de retour de produits de 15% en nous assurant que les clients choisissent le produit le mieux adapté à leurs besoins dès le départ.
                """;
        String prompt = promptTemplate
                .replace("{{TASK_DESCRIPTION}}", taskDescription)
                .replace("{{USER_QUERY}}", userQuery.getQuery());

        var request = AIRequest.builder()
                .model("claude-3-5-sonnet-20241022")
                .maxTokens(1000)
                .temperature(0)
                .messages(new AIRequest.AIMessage[] { new AIRequest.AIMessage("user", prompt)})
                .build();

        return anthropicService.getCompletion(request);
    }
}