package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetSocialRoleCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.SocialRole, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the role that denotes a social. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setSocialRole";
    }

    @Override
    public String commandParameters(){
        return "[roleName]";
    }
}
