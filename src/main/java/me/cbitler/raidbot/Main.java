package me.cbitler.raidbot;

import me.cbitler.raidbot.utility.EnvVariables;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.*;

/**
 * Start the program, read the token, and start the bot
 * @author Christopher Bitler
 */
public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = null;
        try {
            token = readToken();
        } catch (IOException e) {
            System.out.println("Specify Discord Bot Token in .env file. " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        JDA jda = new JDABuilder(token).build();
        jda.awaitReady();
        new RaidBot(jda);
    }

    /**
     * Read the token from the token file
     * @return The token text
     * @throws IOException IOException
     */
    private static String readToken() throws IOException {
        EnvVariables variables = new EnvVariables();
        variables.loadFromEnvFile();
        return variables.getValue("DISCORD_TOKEN");
    }
}
