package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SetRaiderRoleCommand extends Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        if (channel.getGuild().getMember(author).getPermissions().contains(Permission.MANAGE_SERVER)) {
            if (args.length >= 1) {
                try {
                    ;
                    String raiderRole = args[0];
                    RaidBot.getInstance().setRaiderRole(channel.getGuild().getId(), raiderRole);
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Raider role updated to: " + raiderRole).queue());
                } catch (Exception exc) {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for !setRaiderRole: !setRaiderRole [roleName]").queue());
            }
        }
    }
}
