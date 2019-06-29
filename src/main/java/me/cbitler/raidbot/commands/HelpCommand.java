package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand extends Command {
    private final String helpMessage = "GW2-Raid-Bot Help:\n" +
            "Commands:\n" +
            "**!createRaid [raidTest]** - Creates a raid with the specified raid-text. Usable by people with the raid leader role\n" +
            "**!editRaid [raidTest]** - Edits a raids raid-text to the specified one. Usable by people with the raid leader role\n" +
            "**!accept [raid id] [name]** - Accept a player to a raid. Only usable by raid leaders\n" +
            "**!bench [raid id] [name]** - Bench a player for a raid. Only usable by raid leaders\n" +
            "**!removeFromRaid [raid id] [name]** - Remove a player from a raid. Only usable by raid leaders\n" +
            "**!endRaid [raid id]** - End a raid, removing the message and putting it in the archive\n" +
            "**!help** - You are looking at it\n" +
            "**!info** - Information about the bot and it's author\n" +
            "**!setRaidLeaderRole [role]** - Set the role that serves as a raid leader. This is only usable by people with the manage server permission\n" +
            "**!setRaiderRole [role]** - Set the role that serves as a raider. This is only usable by people with the manage server permission\n" +
            "**!setSignupChannel [channel]** - Set the channel that serves as the signup channel. This is only usable by people with the manage server permission\n" +
            "**!setArchiveChannel [channel]** - Set the channel that serves as the archive channel. This is only usable by people with the manage server permission";
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        channel.sendMessage(helpMessage).queue();
    }
}
