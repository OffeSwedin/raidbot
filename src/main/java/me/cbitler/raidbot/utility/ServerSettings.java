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
        if(saveServerSetting(serverId, "raid_leader_role", role)){
            raidLeaderRoleCache.put(serverId, role);
        }
    }

    /**
     * Get the raid leader role for a specific server.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        if (raidLeaderRoleCache.get(serverId) != null) {
            return raidLeaderRoleCache.get(serverId);
        } else {
            String raidleaderRole = loadServerSetting(serverId, "raid_leader_role");
            if(raidleaderRole == null){
                raidleaderRole = "raidleader";
            }
            raidLeaderRoleCache.put(serverId, raidleaderRole);
            return raidleaderRole;
        }
    }

    /**
     * Set the raider role for a server.
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaiderRole(String serverId, String role) {
        if(saveServerSetting(serverId, "raider_role", role)){
            raiderRoleCache.put(serverId, role);
        }
    }

    /**
     * Get the raider role for a specific server.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raider for that server
     */
    public String getRaiderRole(String serverId) {
        if (raiderRoleCache.get(serverId) != null) {
            return raiderRoleCache.get(serverId);
        } else {
            String raiderRole = loadServerSetting(serverId, "raider_role");
            if(raiderRole == null){
                raiderRole = "raider";
            }
            raidLeaderRoleCache.put(serverId, raiderRole);
            return raiderRole;
        }
    }

    /**
     * Set the signup channel for a server.
     * @param serverId The server ID
     * @param channelName The signup channel name
     */
    public void setSignupChannel(String serverId, String channelName) {
        if(saveServerSetting(serverId, "signup_channel", channelName)){
            signupChannelCache.put(serverId, channelName);
        }
    }

    /**
     * Get the signup channel for a specific server.
     * @param serverId the ID of the server
     * @return The signup channel that is set for that server
     */
    public String getSignupChannel(String serverId) {
        if (signupChannelCache.get(serverId) != null) {
            return signupChannelCache.get(serverId);
        } else {
            String signupChannel = loadServerSetting(serverId, "signup_channel");
            if(signupChannel == null){
                signupChannel = "signup";
            }
            signupChannelCache.put(serverId, signupChannel);
            return signupChannel;
        }
    }

    /**
     * Set the archive channel for a server.
     * @param serverId The server ID
     * @param channelName The archive channel name
     */
    public void setArchiveChannel(String serverId, String channelName) {
        if(saveServerSetting(serverId, "archive_channel", channelName)){
            archiveChannelCache.put(serverId, channelName);
        }
    }

    /**
     * Get the archive channel for a specific server.
     * @param serverId the ID of the server
     * @return The archive channel that is set for that server
     */
    public String getArchiveChannel(String serverId) {
        if (archiveChannelCache.get(serverId) != null) {
            return archiveChannelCache.get(serverId);
        } else {
            String archiveChannel = loadServerSetting(serverId, "archive_channel");
            if(archiveChannel == null){
                archiveChannel = "archive";
            }
            archiveChannelCache.put(serverId, archiveChannel);
            return archiveChannel;
        }
    }

    private boolean saveServerSetting(String serverId, String settingName, String settingValue){
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
            return false;
        }

        return true;
    }

    private String loadServerSetting(String serverId, String settingName) {
        try {
            QueryResult results = db.query("SELECT `" + settingName + "` FROM `serverSettings` WHERE `serverId` = ?",
                    new String[]{serverId});
            if (results.getResults().next()) {
                return results.getResults().getString(settingName);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Could not load server settings. ", e);
            return null;
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
