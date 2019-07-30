package me.cbitler.raidbot.commands.setRaidSignupStatusCommands;

import me.cbitler.raidbot.commands.SetRaidSignupStatusCommand;
import me.cbitler.raidbot.raids.Raid;

public class NoShowToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.noShowUser(userName);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Sets players to noShow for a raid. Only usable by raid leaders";
    }

    @Override
    public String commandName() {
        return "noShow";
    }
}
