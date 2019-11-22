package mintey.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class InfoCommand extends Command {
    private final String information = "RaidSignUpBot Information:\n" +
            "Author: Mintey#0942 & Offe#0289\n" +
            "Contact us with any questions.";

    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        channel.sendMessage(information).queue();
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Information about the bot and it's author. ";
    }

    @Override
    public String commandName() {
        return "info";
    }

    @Override
    public String commandParameters() {
        return "";
    }
}
