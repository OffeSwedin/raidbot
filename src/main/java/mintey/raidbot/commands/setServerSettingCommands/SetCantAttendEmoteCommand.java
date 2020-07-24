package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetCantAttendEmoteCommand extends SetServerSettingCommand {

    @Override
    public String handleServerSettingValue(String serverSettingValue) {
        return ParsePossibleEmote(serverSettingValue);
    }

    @Override
    public String serverSettingName(){
        return ServerSettings.CantAttendReaction;
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
