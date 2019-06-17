package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SetRaidleaderRoleCommand implements Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        if (channel.getGuild().getMember(author).getPermissions().contains(Permission.MANAGE_SERVER)) {
            if (args.length >= 1) {
                try {
                    ;
                    String raidLeaderRole = args[0];
                    RaidBot.getInstance().setRaidLeaderRole(channel.getGuild().getId(), raidLeaderRole);
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Raid leader role updated to: " + raidLeaderRole).queue());
                } catch (Exception exc) {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for !removeFromRaid: !removeFromRaid [raid id] [name]").queue());
            }
        }
    }
}
