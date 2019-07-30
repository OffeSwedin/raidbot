package me.cbitler.raidbot.commands.setRaidSignupStatusCommands;

import me.cbitler.raidbot.commands.SetRaidSignupStatusCommand;
import me.cbitler.raidbot.raids.Raid;

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
