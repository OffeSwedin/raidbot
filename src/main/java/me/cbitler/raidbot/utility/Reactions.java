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

            "Tank", //587246785144160256
            "Healer", //588452960661536777
            "Melee", //587246755741958166
            "Ranged", //587246716345122817
            "NotAttending" //587246903369138187
    };

    static Emote[] reactions = {
    		getEmoji("588456264716124162"), // tank
    		getEmoji("588452960661536777"), // healer
    		getEmoji("588455826625265684"), // Melee
    		getEmoji("588456430185480194"), // Ranged
            getEmoji("588459958484598784") // not Attending
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
