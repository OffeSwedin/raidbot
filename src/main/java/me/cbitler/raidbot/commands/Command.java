package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class Command {
    public abstract void handleCommand(String command, String[] args, TextChannel channel, User author);

    protected String combineArguments(String[] parts, int startIndex) {
        StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            text.append(parts[i]).append(" ");
        }

        return text.toString().trim();
    }
}
