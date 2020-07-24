package mintey.raidbot.commands;

import mintey.raidbot.RaidBot;
import mintey.raidbot.utility.ServerSettings;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class ShowServerSettingsCommand extends Command {

    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        String guildId = channel.getGuild().getId();

        StringBuilder serverSettingsMessage = new StringBuilder("Server Settings: ");

        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.RaidLeaderRole, false));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.RaiderRole, false));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.SocialRole, false));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.SignupChannel, false));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.ArchiveChannel, false));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.TankReaction, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.HealerReaction, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.MeleeReaction, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.RangedReaction, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.CantAttendReaction, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.AcceptedEmote, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.BenchedEmote, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.NoShowEmote, true));
        serverSettingsMessage.append(ServerSettingToPrintableString(guildId, ServerSettings.NotDecidedEmote, true));

        channel.sendMessage(serverSettingsMessage.toString()).queue();
    }

    private String ServerSettingToPrintableString(String guildId, String serverSettingToPrint, boolean printAsEmote){
        String serverSettingValue = ServerSettings.getInstance().loadServerSetting(guildId, serverSettingToPrint);
        if(printAsEmote){
            Emote emote = RaidBot.getInstance().getJda().getEmoteById(serverSettingValue);
            serverSettingValue = emote.getAsMention();
        }
        return "\n" + serverSettingToPrint + ": " + serverSettingValue;
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
