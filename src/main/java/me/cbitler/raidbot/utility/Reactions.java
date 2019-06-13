package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {

    static Reaction[] reactions = {
            new Reaction("tank", "587246785144160256"),
            new Reaction("healer", "587244937846849536"),
            new Reaction("melee", "587246716345122817"),
            new Reaction("ranged", "587246755741958166"),
            new Reaction("not attending", "587246903369138187")
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
