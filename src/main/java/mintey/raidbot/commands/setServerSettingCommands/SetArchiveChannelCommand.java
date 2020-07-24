package mintey.raidbot.commands.setServerSettingCommands;

import mintey.raidbot.commands.SetServerSettingCommand;
import mintey.raidbot.utility.ServerSettings;

public class SetArchiveChannelCommand extends SetServerSettingCommand {

    @Override
    public String serverSettingName(){
        return ServerSettings.ArchiveChannel;
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
