package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.utility.ServerSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SetSignupChannelCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        if (channel.getGuild().getMember(author).getPermissions().contains(Permission.MANAGE_SERVER)) {
            if (args.length >= 1) {
                try {
                    String signupChannel = args[0];
                    ServerSettings.getInstance().setSignupChannel(channel.getGuild().getId(), signupChannel);
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Signup channel updated to: " + signupChannel).queue());
                } catch (Exception exc) {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage(helpMessage()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the channel that serves as the signup channel. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setSignupChannel";
    }

    @Override
    public String commandParameters(){
        return "[channelName]";
    }
}
