package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetRaiderRoleCommand extends SetServerSettingCommand {

    @Override
    public String serverSettingName(){
        return ServerSettings.RaiderRole;
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the role that denotes a raider. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setRaiderRole";
    }

    @Override
    public String commandParameters(){
        return "[roleName]";
    }
}
