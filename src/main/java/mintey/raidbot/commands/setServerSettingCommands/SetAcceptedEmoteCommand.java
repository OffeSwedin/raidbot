package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetAcceptedEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.AcceptedEmote, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a Accepted raider. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setAcceptedEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
