package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetCantAttendEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.CantAttendReaction, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes can't attend. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setCantAttendEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
