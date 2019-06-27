package me.cbitler.raidbot.commands;

import java.util.HashMap;

public class CommandRegistry {
    private static final HashMap<String, Command> commands = new HashMap<>();

    public static void addCommand(String commandText, Command cmd) {
        commands.put(commandText.toLowerCase(), cmd);
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
