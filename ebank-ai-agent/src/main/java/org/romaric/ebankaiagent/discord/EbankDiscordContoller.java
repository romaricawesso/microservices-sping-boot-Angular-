package org.romaric.ebankaiagent.discord;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.romaric.ebankaiagent.agents.EbankAiAgent;

@DiscordController
public class EbankDiscordContoller {
    private EbankAiAgent ebankAiAgent;


    public EbankDiscordContoller(EbankAiAgent ebankAiAgent) {
        this.ebankAiAgent = ebankAiAgent;
    }

    @DiscordMapping
    private void perform (MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = ebankAiAgent.chat("C1", query);
        event.getChannel().sendMessage(response).queue();
    }
}
