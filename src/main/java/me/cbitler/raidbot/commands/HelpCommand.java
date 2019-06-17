package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand implements Command {
    private final String helpMessage = "GW2-Raid-Bot Help:\n" +
            "Commands:\n" +
            "**!createRaid** - Start the raid creation process. Usable by people with the raid leader role\n" +
            "**!accept [raid id] [name]** - Accept a player to a raid. Only usable by raid leaders\n" +
            "**!bench [raid id] [name]** - Bench a player for a raid. Only usable by raid leaders\n" +
            "**!removeFromRaid [raid id] [name]** - Remove a player from a raid. Only usable by raid leaders\n" +
            "**!endRaid [raid id] [log link 1] [log link 2] ...** - End a raid, removing the message and DM'ing the users in the raid with log links. The log links are optional arguments\n" +
            "**!help** - You are looking at it\n" +
            "**!info** - Information about the bot and it's author\n" +
            "**!setRaidLeaderRole [role]** - Set the role that serves as a raid leader. This is only usable by people with the manage server permission\n" +
            "**!setRaiderRole [role]** - Set the role that serves as a raider. This is only usable by people with the manage server permission\n" +
            "**!setSignupChannel [channel]** - Set the channel that serves as the signup channel. This is only usable by people with the manage server permission\n" +
            "**!setArchiveChannel [channel]** - Set the channel that serves as the archive channel. This is only usable by people with the manage server permission";
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        channel.sendMessage(helpMessage).queue();
    }
}
