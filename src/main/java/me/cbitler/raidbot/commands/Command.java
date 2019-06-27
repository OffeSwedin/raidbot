package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class Command {
    public abstract void handleCommand(String command, String[] args, TextChannel channel, User author);

    public static String combineArguments(String[] parts, int startIndex) {
        String text = "";
        for (int i = startIndex; i < parts.length; i++) {
            text += (parts[i] + " ");
        }

        return text.trim();
    }
}
