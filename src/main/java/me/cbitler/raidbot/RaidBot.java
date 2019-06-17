package me.cbitler.raidbot;

import me.cbitler.raidbot.commands.*;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.handlers.ChannelMessageHandler;
import me.cbitler.raidbot.handlers.DMHandler;
import me.cbitler.raidbot.handlers.ReactionHandler;
import me.cbitler.raidbot.handlers.RoleChangeHandler;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class representing the raid bot itself.
 * This stores the creation/roleSelection map data and also the list of pendingRaids
 * Additionally, it also stores the database in use by the bot and serves as a way
 * for other classes to access it.
 *
 * @author Christopher Bitler
 */
public class RaidBot {
    private static RaidBot instance;
    private JDA jda;

    private HashMap<String, CreationStep> creation = new HashMap<>();
    private HashMap<String, PendingRaid> pendingRaids = new HashMap<>();

    //TODO: This should be moved to it's own settings thing
    private HashMap<String, String> raidLeaderRoleCache = new HashMap<>();
    private HashMap<String, String> raiderRoleCache = new HashMap<>();
    private HashMap<String, String> signupChannelCache = new HashMap<>();
    private HashMap<String, String> archiveChannelCache = new HashMap<>();

    private Database db;

    /**
     * Create a new instance of the raid bot with the specified JDA api
     * @param jda The API for the bot to use
     */
    public RaidBot(JDA jda) {
        instance = this;

        this.jda = jda;
        jda.addEventListener(new DMHandler(), new ChannelMessageHandler(), new ReactionHandler(), new RoleChangeHandler());
        
        db = new Database();
        db.connect();
        RaidManager.loadRaids();

        CommandRegistry.addCommand("help", new HelpCommand());
        CommandRegistry.addCommand("info", new InfoCommand());
        CommandRegistry.addCommand("endRaid", new EndRaidCommand());
        CommandRegistry.addCommand("createRaid", new CreateRaidCommand());
        CommandRegistry.addCommand("removeFromRaid", new RemoveFromRaidCommand());
        CommandRegistry.addCommand("setRaidleaderRole", new SetRaidleaderRoleCommand());
        CommandRegistry.addCommand("setRaiderRole", new SetRaiderRoleCommand());
        CommandRegistry.addCommand("setSignupChannel", new SetSignupChannelCommand());
        CommandRegistry.addCommand("setArchiveChannel", new SetArchiveChannelCommand());
    }

    /**
     * Map of UserId -> creation step for people in the creation process
     * @return The map of UserId -> creation step for people in the creation process
     */
    public HashMap<String, CreationStep> getCreationMap() {
        return creation;
    }

    /**
     * Map of the UserId -> pendingRaid step for raids in the setup process
     * @return The map of UserId -> pendingRaid
     */
    public HashMap<String, PendingRaid> getPendingRaids() {
        return pendingRaids;
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
     * Get the raid leader role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        if (raidLeaderRoleCache.get(serverId) != null) {
            return raidLeaderRoleCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `raid_leader_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                    raidLeaderRoleCache.put(serverId, results.getResults().getString("raid_leader_role"));
                    return raidLeaderRoleCache.get(serverId);
                } else {
                    return "Raid Leader";
                }
            } catch (Exception e) {
                return "Raid Leader";
            }
        }
    }

    /**
     * Set the raid leader role for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaidLeaderRole(String serverId, String role) {
        raidLeaderRoleCache.put(serverId, role);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`raid_leader_role`) VALUES (?,?)",
                    new String[] { serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `raid_leader_role` = ? WHERE `serverId` = ?",
                        new String[] { role, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }
    
    /**
     * Set the raid leader role for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaiderRole(String serverId, String role) {
        raiderRoleCache.put(serverId, role);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`raider_role`) VALUES (?,?)",
                    new String[] { serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `raider_role` = ? WHERE `serverId` = ?",
                        new String[] { role, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }
    
    /**
     * Get the raider role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaiderRole(String serverId) {
        if (raiderRoleCache.get(serverId) != null) {
            return raiderRoleCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `raider_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                    raiderRoleCache.put(serverId, results.getResults().getString("raider_role"));
                    return raiderRoleCache.get(serverId);
                } else {
                    return "Raider";
                }
            } catch (Exception e) {
                return "Raider";
            }
        }
    }
        
    /**
     * Set the signup channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channelName The signup channel name
     */
    public void setSignupChannel(String serverId, String channelName) {
        signupChannelCache.put(serverId, channelName);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`signup_channel`) VALUES (?,?)",
                    new String[] { serverId, channelName});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `signup_channel` = ? WHERE `serverId` = ?",
                        new String[] { channelName, serverId });
            } catch (SQLException e1) {
            	System.err.println(e1.getMessage());
                // Not much we can do if there is also an insert error
            }
        }
    }
    
    public String getSignupChannel(String serverId) {
        if (signupChannelCache.get(serverId) != null) {
            return signupChannelCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `signup_channel` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                	signupChannelCache.put(serverId, results.getResults().getString("signup_channel"));
                    return signupChannelCache.get(serverId);
                } else {
                    return "SignupChannel";
                }
            } catch (Exception e) {
                return "SignupChannel";
            }
        }
    }
    
    
    /**
     * Set the archive channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channelName The archive channel name
     */
    public void setArchiveChannel(String serverId, String channelName) {
        archiveChannelCache.put(serverId, channelName);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`archive_channel`) VALUES (?,?)",
                    new String[] { serverId, channelName});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `archive_channel` = ? WHERE `serverId` = ?",
                        new String[] { channelName, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }
    
    public String getArchiveChannel(String serverId) {
        if (archiveChannelCache.get(serverId) != null) {
            return archiveChannelCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `archive_channel` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                	archiveChannelCache.put(serverId, results.getResults().getString("archive_channel"));
                    return archiveChannelCache.get(serverId);
                } else {
                    return "Archive";
                }
            } catch (Exception e) {
                return "Archive";
            }
        }
    }
    
    /**
     * Get the current instance of the bot
     * @return The current instance of the bot.
     */
    public static RaidBot getInstance() {
        return instance;
    }

}
