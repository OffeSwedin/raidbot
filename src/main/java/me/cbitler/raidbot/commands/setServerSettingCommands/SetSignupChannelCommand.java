package me.cbitler.raidbot.commands.setServerSettingCommands;

import me.cbitler.raidbot.commands.SetServerSettingCommand;
import me.cbitler.raidbot.utility.ServerSettings;

public class SetSignupChannelCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().saveServerSetting(guildId, ServerSettings.SignupChannel, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the channel that serves as the signup channel. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setSignupChannel";
    }

    @Override
    public String commandParameters(){
        return "[channelName]";
    }
}
