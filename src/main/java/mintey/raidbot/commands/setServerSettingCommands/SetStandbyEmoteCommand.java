package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetStandbyEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.BenchedEmote, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a Standby raider. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setStandbyEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
