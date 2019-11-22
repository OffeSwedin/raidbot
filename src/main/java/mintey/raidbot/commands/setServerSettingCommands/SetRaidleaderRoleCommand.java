package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetRaidleaderRoleCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.RaidLeaderRole, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the role that denotes a raidleader. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setRaidleaderRole";
    }

    @Override
    public String commandParameters(){
        return "[roleName]";
    }
}
