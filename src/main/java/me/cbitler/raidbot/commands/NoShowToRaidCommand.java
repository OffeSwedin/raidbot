package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;

public class NoShowToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.noShowUser(userName);
    }

    @Override
    public String commandName() {
        return "noShow";
    }
}
