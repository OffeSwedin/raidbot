package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetNoShowEmoteCommand extends SetServerSettingCommand {

    @Override
    public String handleServerSettingValue(String serverSettingValue) {
        return ParsePossibleEmote(serverSettingValue);
    }

    @Override
    public String serverSettingName(){
        return ServerSettings.NoShowEmote;
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
