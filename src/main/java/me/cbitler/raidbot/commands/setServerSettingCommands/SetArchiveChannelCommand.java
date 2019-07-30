package me.cbitler.raidbot.commands.setServerSettingCommands;

import me.cbitler.raidbot.commands.SetServerSettingCommand;
import me.cbitler.raidbot.utility.ServerSettings;

public class SetArchiveChannelCommand extends SetServerSettingCommand {

    @Override
    public void handleServerSetting(String guildId, String serverSettingValue) {
        ServerSettings.getInstance().setArchiveChannel(guildId, serverSettingValue);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Set the channel that serves as the archive channel. Only usable by server admins. ";
    }

    @Override
    public String commandName() {
        return "setArchiveChannel";
    }

    @Override
    public String commandParameters(){
        return "[channelName]";
    }
}
