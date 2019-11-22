package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetTankEmoteCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.TankReaction, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the emote that denotes a tank. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setTankEmote";
    }

    @Override
    public String commandParameters(){
        return "[emoteId]";
    }
}
