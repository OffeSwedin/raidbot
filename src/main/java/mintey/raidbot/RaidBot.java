package mintey.raidbot;

import mintey.raidbot.commands.setRaidSignupStatusCommands.AcceptToRaidCommand;
import mintey.raidbot.commands.setRaidSignupStatusCommands.NoShowToRaidCommand;
import mintey.raidbot.commands.setRaidSignupStatusCommands.StandbyToRaidCommand;
import mintey.raidbot.database.Database;
import mintey.raidbot.database.QueryResult;
import mintey.raidbot.handlers.*;
import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.commands.*;
import mintey.raidbot.commands.setServerSettingCommands.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class representing the raid bot itself.
 * This stores the creation/roleSelection map data and also the list of pendingRaids
 * Additionally, it also stores the database in use by the bot and serves as a way
 * for other classes to access it.
 *
 * @author Christopher Bitler
 */
public class RaidBot {
    private static final Logger log = LoggerFactory.getLogger(RaidBot.class);

    private static RaidBot instance;

    private final JDA jda;
    private final Database db;

    /**
     * Create a new instance of the raid bot with the specified JDA api
     * @param jda The API for the bot to use
     */
    public RaidBot(JDA jda) {
        instance = this;
        this.jda = jda;

        db = new Database();
        db.connect();
        setupDatabaseKeepAlive();

        CommandRegistry.addCommand(new HelpCommand());
        CommandRegistry.addCommand(new InfoCommand());
        CommandRegistry.addCommand(new EndRaidCommand());
        CommandRegistry.addCommand(new CreateRaidCommand());
        CommandRegistry.addCommand(new EditRaidCommand());
        CommandRegistry.addCommand(new AcceptToRaidCommand());
        CommandRegistry.addCommand(new StandbyToRaidCommand());
        CommandRegistry.addCommand(new NoShowToRaidCommand());
        CommandRegistry.addCommand(new MoveToRoleCommand());
        CommandRegistry.addCommand(new SetRaidleaderRoleCommand());
        CommandRegistry.addCommand(new SetRaiderRoleCommand());
        CommandRegistry.addCommand(new SetSignupChannelCommand());
        CommandRegistry.addCommand(new SetArchiveChannelCommand());
        CommandRegistry.addCommand(new SetSocialRoleCommand());
        CommandRegistry.addCommand(new RemindRaidCommand());
        CommandRegistry.addCommand(new SetTankEmoteCommand());
        CommandRegistry.addCommand(new SetHealerEmoteCommand());
        CommandRegistry.addCommand(new SetMeleeEmoteCommand());
        CommandRegistry.addCommand(new SetRangedEmoteCommand());
        CommandRegistry.addCommand(new SetCantAttendEmoteCommand());
        CommandRegistry.addCommand(new SetAcceptedEmoteCommand());
        CommandRegistry.addCommand(new SetStandbyEmoteCommand());
        CommandRegistry.addCommand(new SetNoShowEmoteCommand());
        CommandRegistry.addCommand(new SetNotDecidedEmoteCommand());

        RaidManager.loadRaidsFromDatabase();

        jda.addEventListener(new ChannelMessageHandler(), new ReactionHandler(), new RoleChangeHandler(),
                new LeaveHandler(), new NicknameChangeHandler());
    }

    private void setupDatabaseKeepAlive() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try{
                QueryResult results = db.query("SELECT * FROM `serverSettings`", new String[]{});

                results.getResults().close();
                results.getStmt().close();
            } catch(SQLException e){
                log.error("Failed with SQL query. ", e);
            }
        };
        ses.scheduleAtFixedRate(task, 0, 15, TimeUnit.MINUTES);
    }

    /**
     * Get the JDA server object related to the server ID
     * @param id The server ID
     * @return The server related to that that ID
     */
    public Guild getServer(String id) {
        return jda.getGuildById(id);
    }

    /**
     * Exposes the underlying library. This is mainly necessary for getting Emojis
     * @return The JDA library object
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the database that the bot is using
     * @return The database that the bot is using
     */
    public Database getDatabase() {
        return db;
    }
    
    /**
     * Get the current instance of the bot
     * @return The current instance of the bot.
     */
    public static RaidBot getInstance() {
        return instance;
    }

}