package org.romaric.ebankaiagent.Rest;

import org.romaric.ebankaiagent.agents.ConversationMemory;
import org.romaric.ebankaiagent.agents.EbankAiAgent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class EbankAgentRestController {

    private final EbankAiAgent ebankAiAgent;
    private final ConversationMemory conversationMemory;

    public EbankAgentRestController(EbankAiAgent ebankAiAgent, ConversationMemory conversationMemory){
        this.ebankAiAgent = ebankAiAgent;
        this.conversationMemory = conversationMemory;
    }

    @GetMapping
    public String chat(@RequestParam(value = "query", defaultValue = "Hello") String query,
                       @RequestParam(value = "sessionId", required = false) String sessionId){
        String id = (sessionId != null && !sessionId.isEmpty()) ? sessionId : UUID.randomUUID().toString();
        return ebankAiAgent.chat(id, query);
    }

    @PostMapping
    public String chatPost(@RequestBody String query,
                           @RequestParam(value = "sessionId", required = false) String sessionId){
        String id = (sessionId != null && !sessionId.isEmpty()) ? sessionId : UUID.randomUUID().toString();
        return ebankAiAgent.chat(id, query);
    }

    // --- Endpoints pour gérer la mémoire de conversation ---

    @GetMapping("/memory/history")
    public List<String> getHistory(@RequestParam(value = "sessionId") String sessionId){
        return conversationMemory.getHistory(sessionId);
    }

    @DeleteMapping("/memory/clear")
    public Map<String, String> clearHistory(@RequestParam(value = "sessionId") String sessionId){
        conversationMemory.clearHistory(sessionId);
        return Map.of("message", "Historique effacé pour la session: " + sessionId);
    }

    @GetMapping("/memory/count")
    public Map<String, Object> getMessageCount(@RequestParam(value = "sessionId") String sessionId){
        int count = conversationMemory.getMessageCount(sessionId);
        return Map.of("sessionId", sessionId, "messageCount", count);
    }
}
