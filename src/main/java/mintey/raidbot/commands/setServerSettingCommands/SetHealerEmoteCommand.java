package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetHealerEmoteCommand extends SetServerSettingCommand {

    @Override
    public String handleServerSettingValue(String serverSettingValue) {
        return ParsePossibleEmote(serverSettingValue);
    }

    @Override
    public String serverSettingName(){
        return ServerSettings.HealerReaction;
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a healer. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setHealerEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
