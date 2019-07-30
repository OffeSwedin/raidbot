package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;

public class ServerSettings {
    private static final Logger log = LoggerFactory.getLogger(ServerSettings.class);

    private static ServerSettings instance;
    private final Database db;

    private final HashMap<String, String> raidLeaderRoleCache = new HashMap<>();
    private final HashMap<String, String> raiderRoleCache = new HashMap<>();
    private final HashMap<String, String> socialRoleCache = new HashMap<>();
    private final HashMap<String, String> signupChannelCache = new HashMap<>();
    private final HashMap<String, String> archiveChannelCache = new HashMap<>();

    private ServerSettings(Database db){
        this.db = db;
    }

    /**
     * Set the raid leader role for a server.
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaidLeaderRole(String serverId, String role) {
        saveServerSetting(serverId, "raid_leader_role", role, raidLeaderRoleCache);
    }

    /**
     * Get the raid leader role for a specific server.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        return loadServerSetting(serverId, "raid_leader_role", raidLeaderRoleCache, "raidleader");
    }

    /**
     * Set the raider role for a server.
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaiderRole(String serverId, String role) {
        saveServerSetting(serverId, "raider_role", role, raiderRoleCache);
    }

    /**
     * Get the raider role for a specific server.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raider for that server
     */
    public String getRaiderRole(String serverId) {
        return loadServerSetting(serverId, "raider_role", raiderRoleCache, "raider");
    }

    /**
     * Set the raider role for a server.
     * @param serverId The server ID
     * @param role The role name
     */
    public void setSocialRole(String serverId, String role) {
        saveServerSetting(serverId, "social_role", role, socialRoleCache);
    }

    /**
     * Get the raider role for a specific server.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raider for that server
     */
    public String getSocialRole(String serverId) {
        return loadServerSetting(serverId, "social_role", socialRoleCache, "social");
    }

    /**
     * Set the signup channel for a server.
     * @param serverId The server ID
     * @param channelName The signup channel name
     */
    public void setSignupChannel(String serverId, String channelName) {
        saveServerSetting(serverId, "signup_channel", channelName, signupChannelCache);
    }

    /**
     * Get the signup channel for a specific server.
     * @param serverId the ID of the server
     * @return The signup channel that is set for that server
     */
    public String getSignupChannel(String serverId) {
        return loadServerSetting(serverId, "signup_channel", signupChannelCache, "signup");
    }

    /**
     * Set the archive channel for a server.
     * @param serverId The server ID
     * @param channelName The archive channel name
     */
    public void setArchiveChannel(String serverId, String channelName) {
        saveServerSetting(serverId, "archive_channel", channelName, archiveChannelCache);
    }

    /**
     * Get the archive channel for a specific server.
     * @param serverId the ID of the server
     * @return The archive channel that is set for that server
     */
    public String getArchiveChannel(String serverId) {
        return loadServerSetting(serverId, "archive_channel", archiveChannelCache, "archive");
    }

    private void saveServerSetting(String serverId, String settingName, String settingValue, HashMap<String, String> cache){
        try {
            QueryResult results = db.query("SELECT COUNT(*) FROM `serverSettings` WHERE `serverId` = ?", new String[] { serverId });
            results.getResults().next();
            int count = Integer.parseInt(results.getResults().getString("COUNT(*)"));

            results.getResults().close();
            results.getStmt().close();

            if (count == 0) {
                db.update("INSERT INTO `serverSettings` (`serverId`,`" + settingName + "`) VALUES (?,?)",
                        new String[] { serverId, settingValue});
            } else {
                db.update("UPDATE `serverSettings` SET `" + settingName + "` = ? WHERE `serverId` = ?",
                        new String[] { settingValue, serverId });
            }
        } catch (SQLException e) {
            log.error("Could not update server setting. ", e);
        }

        cache.put(serverId, settingValue);
    }

    private String loadServerSetting(String serverId, String settingName, HashMap<String, String> cache, String defaultRole) {
        if (cache.get(serverId) != null) {
            return cache.get(serverId);
        }

        try {
            QueryResult results = db.query("SELECT `" + settingName + "` FROM `serverSettings` WHERE `serverId` = ?",
                    new String[]{serverId});
            if (results.getResults().next()) {
                String raidleaderRole = results.getResults().getString(settingName);
                cache.put(serverId, raidleaderRole);
                return raidleaderRole;
            } else {
                return defaultRole;
            }
        } catch (Exception e) {
            log.error("Could not load server settings. ", e);
            return defaultRole;
        }
    }

    /**
     * Get the current instance of the serversettings
     * @return The current instance of the serversettings.
     */
    public static ServerSettings getInstance() {
        if(instance == null){
            instance = new ServerSettings(RaidBot.getInstance().getDatabase());
        }

        return instance;
    }
}
