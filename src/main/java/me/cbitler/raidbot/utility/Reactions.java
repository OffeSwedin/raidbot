package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {

    private final static Reaction[] reactions = {
            new Reaction("Tank", "588456264716124162"),
            new Reaction("Healer", "588452960661536777"),
            new Reaction("Melee", "588455826625265684"),
            new Reaction("Ranged", "588456430185480194"),
            new Reaction("Not Attending", "588459958484598784")
    };

    private final static Reaction[] testReactions = {
            new Reaction("Tank", "587246785144160256"),
            new Reaction("Healer", "587244937846849536"),
            new Reaction("Melee", "587246755741958166"),
            new Reaction("Ranged", "587246716345122817"),
            new Reaction("Not Attending", "587246903369138187")
    };

    private static Reaction[] reactionsToUse = testReactions;

    public static List<Reaction> getReactions(){return new ArrayList<>(Arrays.asList(reactionsToUse)); }

    public static Reaction getReactionFromEmojiId(String emojiId){
        for(Reaction reaction : reactionsToUse){
            if(reaction.getEmote().getId().equals(emojiId)){
                return reaction;
            }
        }

        return null;
    }
}
