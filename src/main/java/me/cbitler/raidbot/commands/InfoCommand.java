package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class InfoCommand extends Command {
    private final String information = "RaidSignUpBot Information:\n" +
            "Author: Mintey#0942 & Offe#0289\n" +
            "Contact us with any questions.";

    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        channel.sendMessage(information).queue();
    }
}
