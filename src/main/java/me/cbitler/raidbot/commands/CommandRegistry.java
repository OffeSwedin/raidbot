package me.cbitler.raidbot.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CommandRegistry {
    private static final HashMap<String, Command> commands = new HashMap<>();

    public static List<Command> getAllCommands(){
        ArrayList commandList = new ArrayList(commands.values());
        commandList.sort(Comparator.comparing((Command c) -> c.commandName()));
        return commandList;
    }

    public static void addCommand(Command cmd) {
        commands.put(cmd.commandName().toLowerCase(), cmd);
    }

    public static Command getCommand(String command) {
        return commands.get(command.toLowerCase());
    }

    public static String[] getArguments(String[] messageParts) {
        if(messageParts.length != 1) {
            String[] args = new String[messageParts.length - 1];
            System.arraycopy(messageParts, 1, args, 0, messageParts.length - 1);

            return args;
        } else {
            return new String[0];
        }
    }
}
