package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetNoShowEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.NoShowEmote, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a raider who no showed. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setNoShowEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
