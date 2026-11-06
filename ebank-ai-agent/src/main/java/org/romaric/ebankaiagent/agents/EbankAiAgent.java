package org.romaric.ebankaiagent.agents;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EbankAiAgent {
    private static final Logger log = LoggerFactory.getLogger(EbankAiAgent.class);
    private final ChatClient chatClient;
    private final ConversationMemory conversationMemory;

    public EbankAiAgent(ChatModel chatModel,
                        ObjectProvider<ToolCallbackProvider> toolsProvider,
                        ConversationMemory conversationMemory){
        var builder = ChatClient.builder(chatModel);
        var tools = toolsProvider.getIfAvailable();
        if (tools != null) {
            log.info("Number of MCP tool callbacks injected: {}", tools.getToolCallbacks().length);
            for (var tool : tools.getToolCallbacks()) {
                log.info("  - Tool: {}", tool.getToolDefinition().name());
            }
            builder.defaultTools(tools);
        } else {
            log.warn("No ToolCallbackProvider found - MCP tools will not be available");
        }
        this.chatClient = builder.build();
        this.conversationMemory = conversationMemory;
    }

    public String chat(String sessionId, String query){
        // Récupérer l'historique
        List<String> history = conversationMemory.getHistory(sessionId);

        // Construire le prompt avec le dialogue complet
        String prompt;
        if (history.isEmpty()) {
            prompt = query;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Voici l'historique de notre conversation. Continue la conversation naturellement en répondant au dernier message :\n\n");
            for (int i = 0; i < history.size(); i++) {
                String msg = history.get(i);
                sb.append(msg).append("\n");
            }
            sb.append("Utilisateur: ").append(query).append("\n");
            sb.append("Assistant:");
            prompt = sb.toString();
        }

        // Sauvegarder le message de l'utilisateur
        conversationMemory.addMessage(sessionId, "Utilisateur: " + query);

        // Log du prompt complet pour le débogage
        log.info("=== PROMPT ENVOYE ===\n{}\n=== FIN PROMPT ===", prompt);

        // Appeler l'IA
        String response = chatClient.prompt(prompt)
                .call()
                .content();

        // Sauvegarder la réponse
        conversationMemory.addMessage(sessionId, "Assistant: " + response);

        log.debug("Session {} - Messages: {}", sessionId, conversationMemory.getMessageCount(sessionId));

        return response;
    }
}
