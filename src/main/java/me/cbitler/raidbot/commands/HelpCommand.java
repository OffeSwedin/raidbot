package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand extends Command {
    private final String helpMessage =
            "Commands:\n" +
            "**!createRaid [raidText]** - Creates a raid with the specified raid-text. Only usable by raid leaders\n" +
            "**!editRaid [raidText]** - Edits a raids raid-text to the specified one. Only usable by raid leaders\n" +
            "**!endRaid [raidId]** - End a raid, removing the message and putting it in the archive. Only usable by raid leaders\n" +
            "**!accept [raidId] [userName] [userName] [...]** - Accepts players to a raid. Only usable by raid leaders\n" +
            "**!bench [raidId] [userName] [userName] [...]** - Benches players for a raid. Only usable by raid leaders\n" +
            "**!moveToRole [raidId] [userName] [roleName]** - Set a player as the specified role in a raid. Only usable by raid leaders\n" +
            "**!setRaidLeaderRole [roleName]** - Set the role that serves as a raid leader. Only usable by server admins\n" +
            "**!setRaiderRole [roleName]** - Set the role that serves as a raider. Only usable by server admins\n" +
            "**!setSignupChannel [channelName]** - Set the channel that serves as the signup channel. Only usable by server admins\n" +
            "**!setArchiveChannel [channelName]** - Set the channel that serves as the archive channel. Only usable by server admins\n" +
            "**!help** - You are looking at it\n" +
            "**!info** - Information about the bot and it's author\n";
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        channel.sendMessage(helpMessage).queue();
    }
}
