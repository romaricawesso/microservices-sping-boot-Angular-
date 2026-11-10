package org.romaric.ebankaiagent.repository;

import org.romaric.ebankaiagent.domain.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {

    List<ConversationMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Modifying
    @Query("DELETE FROM ConversationMessage cm WHERE cm.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    long countBySessionId(String sessionId);
}
