package org.romaric.ebankaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {com.zgamelogic.discord.components.DiscordBot.class})
public class EbankAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankAiAgentApplication.class, args);
    }

}
