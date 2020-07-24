package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetMeleeEmoteCommand extends SetServerSettingCommand {

    @Override
    public String handleServerSettingValue(String serverSettingValue) {
        return ParsePossibleEmote(serverSettingValue);
    }

    @Override
    public String serverSettingName(){
        return ServerSettings.MeleeReaction;
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a melee. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setMeleeEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
