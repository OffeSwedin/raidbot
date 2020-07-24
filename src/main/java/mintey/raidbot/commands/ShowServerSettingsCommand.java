package mintey.raidbot.commands;

import mintey.raidbot.utility.ServerSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class ShowServerSettingsCommand extends Command {

    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        StringBuilder serverSettingsMessage = new StringBuilder("Server Settings: ");

        serverSettingsMessage.append(ServerSettings.RaidLeaderRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaidLeaderRole));
        serverSettingsMessage.append(ServerSettings.RaiderRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaiderRole));
        serverSettingsMessage.append(ServerSettings.SocialRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.SocialRole));
        serverSettingsMessage.append(ServerSettings.SignupChannel + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.SignupChannel));
        serverSettingsMessage.append(ServerSettings.ArchiveChannel + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.ArchiveChannel));
        serverSettingsMessage.append(ServerSettings.TankReaction + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.TankReaction));
        serverSettingsMessage.append(ServerSettings.HealerReaction + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.HealerReaction));
        serverSettingsMessage.append(ServerSettings.MeleeReaction + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.MeleeReaction));
        serverSettingsMessage.append(ServerSettings.RangedReaction + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RangedReaction));
        serverSettingsMessage.append(ServerSettings.CantAttendReaction + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.CantAttendReaction));
        serverSettingsMessage.append(ServerSettings.AcceptedEmote + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.AcceptedEmote));
        serverSettingsMessage.append(ServerSettings.BenchedEmote + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.BenchedEmote));
        serverSettingsMessage.append(ServerSettings.NoShowEmote + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.NoShowEmote));
        serverSettingsMessage.append(ServerSettings.NotDecidedEmote + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.NotDecidedEmote));

        channel.sendMessage(serverSettingsMessage.toString()).queue();
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Show the currently set values for all server settings. ";
    }

    @Override
    public String commandName() {
        return "showServerSettings";
    }

    @Override
    public String commandParameters() {
        return "";
    }
}
