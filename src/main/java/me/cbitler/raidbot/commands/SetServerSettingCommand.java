package me.cbitler.raidbot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class SetServerSettingCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        if (channel.getGuild().getMember(author).getPermissions().contains(Permission.MANAGE_SERVER)) {
            if (args.length >= 1) {
                try {
                    String serverSettingValue = args[0];
                    handleServerSetting(channel.getGuild().getId(), serverSettingValue);
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Setting successfully updated to: " + serverSettingValue).queue());
                } catch (Exception exc) {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    public abstract String helpMessage();

    public abstract String commandName();

    public abstract String commandParameters();

    public abstract void handleServerSetting(String guildId, String serverSettingValue);
}
