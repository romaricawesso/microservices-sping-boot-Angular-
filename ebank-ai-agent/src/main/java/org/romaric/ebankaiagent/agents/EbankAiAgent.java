package org.romaric.ebankaiagent.agents;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class EbankAiAgent {
    private static final Logger log = LoggerFactory.getLogger(EbankAiAgent.class);
    private ChatClient chatClient;

    public EbankAiAgent(ChatModel chatModel, ObjectProvider<ToolCallbackProvider> toolsProvider){
        var builder = ChatClient.builder(chatModel);
        var tools = toolsProvider.getIfAvailable();
        if (tools != null) {
            log.info("Number of MCP tool callbacks injected: {}", tools.getToolCallbacks().length);
            for (var tool : tools.getToolCallbacks()) {
                log.info("  - Tool: {}", tool.getToolDefinition().name());
            }
            builder.defaultToolCallbacks(tools);
        } else {
            log.warn("No ToolCallbackProvider found - MCP tools will not be available");
        }
        this.chatClient = builder.build();
    }

    public String chat(String query){
        return chatClient.prompt(query).call().content();
    }
}
