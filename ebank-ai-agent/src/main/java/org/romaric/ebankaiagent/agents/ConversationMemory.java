package org.romaric.ebankaiagent.agents;

import org.romaric.ebankaiagent.domain.ConversationMessage;
import org.romaric.ebankaiagent.repository.ConversationMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationMemory {

    private final ConversationMessageRepository repository;

    public ConversationMemory(ConversationMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addMessage(String sessionId, String message) {
        // Extraire le rôle et le contenu depuis le format "Rôle: contenu"
        String role;
        String content;
        if (message.contains(": ")) {
            int index = message.indexOf(": ");
            role = message.substring(0, index);
            content = message.substring(index + 2);
        } else {
            role = "Utilisateur";
            content = message;
        }
        repository.save(new ConversationMessage(sessionId, role, content));
    }

    @Transactional(readOnly = true)
    public List<String> getHistory(String sessionId) {
        return repository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearHistory(String sessionId) {
        repository.deleteBySessionId(sessionId);
    }

    @Transactional(readOnly = true)
    public int getMessageCount(String sessionId) {
        return (int) repository.countBySessionId(sessionId);
    }
}
