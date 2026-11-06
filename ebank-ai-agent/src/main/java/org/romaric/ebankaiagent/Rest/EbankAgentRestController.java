package org.romaric.ebankaiagent.Rest;

import org.romaric.ebankaiagent.agents.EbankAiAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class EbankAgentRestController {

    private final EbankAiAgent ebankAiAgent;

    public EbankAgentRestController(EbankAiAgent ebankAiAgent){
        this.ebankAiAgent = ebankAiAgent;
    }

    @GetMapping
    public String chat(@RequestParam(defaultValue = "Hello") String query,
                       @RequestParam(required = false) String sessionId){
        String id = (sessionId != null && !sessionId.isEmpty()) ? sessionId : UUID.randomUUID().toString();
        return ebankAiAgent.chat(id, query);
    }

    @PostMapping
    public String chatPost(@RequestBody String query,
                           @RequestParam(required = false) String sessionId){
        String id = (sessionId != null && !sessionId.isEmpty()) ? sessionId : UUID.randomUUID().toString();
        return ebankAiAgent.chat(id, query);
    }
}
