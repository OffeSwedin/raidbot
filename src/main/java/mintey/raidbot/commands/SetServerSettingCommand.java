package mintey.raidbot.commands;

import mintey.raidbot.RaidBot;
import mintey.raidbot.utility.ServerSettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class SetServerSettingCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Member member = channel.getGuild().retrieveMember(author).complete();
        if (member != null && member.getPermissions().contains(Permission.MANAGE_SERVER)) {
            if (args.length >= 1) {
                try {
                    String serverSettingValue = args[0];
                    ServerSettings.getInstance().saveServerSetting(channel.getGuild().getId(), serverSettingName(), handleServerSettingValue(serverSettingValue));
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Setting successfully updated to: " + serverSettingValue).queue());
                } catch (Exception exc) {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Invalid value for setting. ").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    public String ParsePossibleEmote(String input){
        String[] parts = input.split("<|>|:");
        if(parts.length > 1){
            input = parts[3];
        }

        return RaidBot.getInstance().getJda().getEmoteById(input).getId();
    }

    public String handleServerSettingValue(String serverSettingValue){
        return serverSettingValue;
    }

    public abstract String serverSettingName();

    public abstract String helpMessage();

    public abstract String commandName();

    public abstract String commandParameters();
}
