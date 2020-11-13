package mintey.raidbot;

import mintey.raidbot.utility.EnvVariables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

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

        JDA jda = JDABuilder.createDefault(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        jda.awaitReady();

        for(Guild guild : jda.getGuilds()){
            guild.loadMembers().get();
        }

        new RaidBot(jda);
    }
    private static String readToken() throws IOException {
        EnvVariables variables = new EnvVariables();
        variables.loadFromEnvFile();
        return variables.getValue("DISCORD_TOKEN");
    }
}
