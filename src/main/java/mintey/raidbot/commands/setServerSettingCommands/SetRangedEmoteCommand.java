package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetRangedEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.RangedReaction, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a ranged. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setRangedEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
