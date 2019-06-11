package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] specs = {

            "tank", //587246785144160256
            "healer", //587244937846849536
            "melee", //587246755741958166
            "ranged", //587246716345122817
            "notAttending" //587246903369138187
    };

    static Emote[] reactions = {
    		getEmoji("587246785144160256"), // tank
    		getEmoji("587244937846849536"), // healer
    		getEmoji("587246755741958166"), // Melee
    		getEmoji("587246716345122817"), // Ranged
            getEmoji("587246903369138187") // not Attending
    };

    /**
     * Get an emoji from it's emote ID via JDA
     * @param id The ID of the emoji
     * @return The emote object representing that emoji
     */
    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    /**
     * Get the list of reaction names as a list
     * @return The list of reactions as a list
     */
    public static List<String> getSpecs() {
        return new ArrayList<>(Arrays.asList(specs));
    }

    /**
     * Get the list of emote objects
     * @return The emotes
     */
    public static List<Emote> getEmotes() {
        return new ArrayList<>(Arrays.asList(reactions));
    }


}
