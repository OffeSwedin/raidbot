package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.utility.ServerSettings;

public class SetSocialRoleCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().setSocialRole(guildId, serverSettingValue);
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
