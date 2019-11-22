package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    public static List<Reaction> getSignupReactions(String serverId){
        Reaction[] reactionsToUse = getSignupReactionForServer(serverId);

        return new ArrayList<>(Arrays.asList(reactionsToUse));
    }

    public static Reaction getSignupReactionFromEmojiId(String emojiId, String serverId){
        Reaction[] reactionsToUse = getSignupReactionForServer(serverId);

        for(Reaction reaction : reactionsToUse){
            if(reaction.getEmote().getId().equals(emojiId)){
                return reaction;
            }
        }

        return null;
    }

    private static Reaction[] getSignupReactionForServer(String serverId){
        ServerSettings settings = ServerSettings.getInstance();
        return new Reaction[]{
                new Reaction("Tank", settings.loadServerSetting(serverId, ServerSettings.TankReaction)),
                new Reaction("Healer", settings.loadServerSetting(serverId, ServerSettings.HealerReaction)),
                new Reaction("Melee", settings.loadServerSetting(serverId, ServerSettings.MeleeReaction)),
                new Reaction("Ranged", settings.loadServerSetting(serverId, ServerSettings.RangedReaction)),
                new Reaction("Can't Attend", settings.loadServerSetting(serverId, ServerSettings.CantAttendReaction))
        };
    }
}
