package org.romaric.ebankaiagent.Rest;

import org.romaric.ebankaiagent.agents.EbankAiAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class EbankAgentRestController {

    private final EbankAiAgent ebankAiAgent;

    public EbankAgentRestController(EbankAiAgent ebankAiAgent){
        this.ebankAiAgent = ebankAiAgent;
    }

    @GetMapping
    public String chat(@RequestParam(defaultValue = "Hello") String query){
        return ebankAiAgent.chat(query);
    }

    @PostMapping
    public String chatPost(@RequestBody String query){
        return ebankAiAgent.chat(query);
    }
}
