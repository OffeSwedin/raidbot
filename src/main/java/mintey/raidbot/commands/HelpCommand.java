package mintey.raidbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand extends Command {

    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        StringBuilder helpMessage = new StringBuilder("Commands: ");

        for(Command command : CommandRegistry.getAllCommands()){
            if(!SetServerSettingCommand.class.isAssignableFrom(command.getClass())){
                helpMessage.append("\n").append(command.helpMessage());
            }
        }

        channel.sendMessage(helpMessage.toString()).queue();

        StringBuilder helpMessage2 = new StringBuilder();

        for(Command command : CommandRegistry.getAllCommands()){
            if(SetServerSettingCommand.class.isAssignableFrom(command.getClass())){
                helpMessage2.append("\n").append(command.helpMessage());
            }
        }

        channel.sendMessage(helpMessage2.toString()).queue();
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Your looking at it. ";
    }

    @Override
    public String commandName() {
        return "help";
    }

    @Override
    public String commandParameters() {
        return "";
    }
}
