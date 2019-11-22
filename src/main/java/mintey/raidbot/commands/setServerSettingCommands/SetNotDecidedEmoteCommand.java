package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetNotDecidedEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.NotDecidedEmote, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes not decided. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setNotDecidedEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
