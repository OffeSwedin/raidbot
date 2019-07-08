package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;

public class StandbyToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.standbyUser(userName);
    }

    @Override
    public String commandName() {
        return "standby";
    }
}
