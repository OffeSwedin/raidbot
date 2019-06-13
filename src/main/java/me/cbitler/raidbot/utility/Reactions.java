package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {

    static Reaction[] reactions = {
            new Reaction("Tank", "588456264716124162"),
            new Reaction("Healer", "588452960661536777"),
            new Reaction("Melee", "588455826625265684"),
            new Reaction("Ranged", "588456430185480194"),
            new Reaction("Not Attending", "588459958484598784")
    };

    public static List<Reaction> getReactions(){return new ArrayList<>(Arrays.asList(reactions)); }

    public static Reaction getReactionFromEmojiId(String emojiId){
        for(Reaction reaction : reactions){
            if(reaction.getEmote().getId().equals(emojiId)){
                return reaction;
            }
        }

        return null;
    }
}
