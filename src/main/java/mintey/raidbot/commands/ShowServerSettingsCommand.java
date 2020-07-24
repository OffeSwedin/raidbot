package mintey.raidbot.commands;

import mintey.raidbot.RaidBot;
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

        serverSettingsMessage.append("\n" + ServerSettings.RaidLeaderRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaidLeaderRole));
        serverSettingsMessage.append("\n" + ServerSettings.RaiderRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaiderRole));
        serverSettingsMessage.append("\n" + ServerSettings.SocialRole + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.SocialRole));
        serverSettingsMessage.append("\n" + ServerSettings.SignupChannel + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.SignupChannel));
        serverSettingsMessage.append("\n" + ServerSettings.ArchiveChannel + ": " + ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.ArchiveChannel));
        serverSettingsMessage.append("\n" + ServerSettings.TankReaction + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.TankReaction)));
        serverSettingsMessage.append("\n" + ServerSettings.HealerReaction + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.HealerReaction)));
        serverSettingsMessage.append("\n" + ServerSettings.MeleeReaction + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.MeleeReaction)));
        serverSettingsMessage.append("\n" + ServerSettings.RangedReaction + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RangedReaction)));
        serverSettingsMessage.append("\n" + ServerSettings.CantAttendReaction + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.CantAttendReaction)));
        serverSettingsMessage.append("\n" + ServerSettings.AcceptedEmote + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.AcceptedEmote)));
        serverSettingsMessage.append("\n" + ServerSettings.BenchedEmote + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.BenchedEmote)));
        serverSettingsMessage.append("\n" + ServerSettings.NoShowEmote + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.NoShowEmote)));
        serverSettingsMessage.append("\n" + ServerSettings.NotDecidedEmote + ": " + RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.NotDecidedEmote)));

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
