package mintey.raidbot.commands.setRaidSignupStatusCommands;

import mintey.raidbot.commands.SetRaidSignupStatusCommand;
import mintey.raidbot.raids.Raid;

public class StandbyToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public String helpMessage() {
        return commandFormat() + " - Standby players to a raid. Only usable by raid leaders";
    }

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.standbyUser(userName);
    }

    @Override
    public String commandName() {
        return "standby";
    }
}
