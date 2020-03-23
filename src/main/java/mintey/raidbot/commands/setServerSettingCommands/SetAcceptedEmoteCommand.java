package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetAcceptedEmoteCommand extends SetServerSettingCommand {

    @Override
    public String handleServerSettingValue(String serverSettingValue) {
        return ParsePossibleEmote(serverSettingValue);
    }

    @Override
    public String serverSettingName(){
        return ServerSettings.AcceptedEmote;
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
