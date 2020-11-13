package mintey.raidbot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
    public abstract void handleCommand(String[] args, TextChannel channel, User author);

    public abstract String commandName();

    public abstract String commandParameters();

    public abstract String helpMessage();

    public String commandFormat(){
        return "**!" + commandName() + " " + commandParameters() + "**";
    }

    protected String combineArguments(String[] parts, int startIndex) {
        StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            text.append(parts[i]).append(" ");
        }

        return text.toString().trim();
    }
}
