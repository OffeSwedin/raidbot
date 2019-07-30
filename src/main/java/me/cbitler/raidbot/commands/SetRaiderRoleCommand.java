package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.utility.ServerSettings;

public class SetRaiderRoleCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().setRaiderRole(guildId, serverSettingValue);
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
