package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetRaidleaderRoleCommand extends SetServerSettingCommand {

    @Override
    public String serverSettingName(){
        return ServerSettings.RaidLeaderRole;
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
