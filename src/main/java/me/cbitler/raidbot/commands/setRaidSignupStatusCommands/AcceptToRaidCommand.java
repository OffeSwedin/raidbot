package me.cbitler.raidbot.commands.setRaidSignupStatusCommands;

import me.cbitler.raidbot.commands.SetRaidSignupStatusCommand;
import me.cbitler.raidbot.raids.Raid;

public class AcceptToRaidCommand extends SetRaidSignupStatusCommand {

    @Override
    public boolean handleUser(Raid raid, String userName) {
        return raid.acceptUser(userName);
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Accepts players to a raid. Only usable by raid leaders";
    }

    @Override
    public String commandName() {
        return "accept";
    }
}
