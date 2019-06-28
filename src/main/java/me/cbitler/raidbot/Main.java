package me.cbitler.raidbot;

import me.cbitler.raidbot.utility.EnvVariables;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Start the program, read the token, and start the bot
 * @author Christopher Bitler
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = null;
        try {
            token = readToken();
        } catch (IOException e) {
            log.error("Specify Discord Bot Token in .env file. ", e);
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
