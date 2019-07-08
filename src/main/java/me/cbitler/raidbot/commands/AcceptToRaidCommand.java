package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;

public class AcceptToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.acceptUser(userName);
    }

    @Override
    public String commandName() {
        return "accept";
    }
}
