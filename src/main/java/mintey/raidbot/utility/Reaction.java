package mintey.raidbot.utility;

import mintey.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

public class Reaction {
    private final String spec;
    private final Emote emote;

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
