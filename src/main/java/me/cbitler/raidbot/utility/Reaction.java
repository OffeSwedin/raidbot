package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

public class Reaction {
    private String spec;
    private Emote emote;

    public Reaction(String spec, String emoteId){
        this.spec = spec;
        this.emote = getEmoji(emoteId);
    }

    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    public Emote getEmote(){
        return this.emote;
    }

    public String getSpec(){
        return this.spec;
    }
}
