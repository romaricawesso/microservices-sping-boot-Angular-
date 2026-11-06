package org.romaric.ebankaiagent.agents;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationMemory {

    private final Map<String, List<String>> conversations = new ConcurrentHashMap<>();

    public void addMessage(String sessionId, String message) {
        conversations
                .computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(message);
    }

    public List<String> getHistory(String sessionId) {
        return conversations.getOrDefault(sessionId, new ArrayList<>());
    }

    public void clearHistory(String sessionId) {
        conversations.remove(sessionId);
    }

    public int getMessageCount(String sessionId) {
        return conversations.getOrDefault(sessionId, new ArrayList<>()).size();
    }
}
