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

    private final HashMap<String, HashMap<String, String>> cache = new HashMap<>();

    private ServerSettings(Database db){
        this.db = db;
    }

    public static final String RaidLeaderRole = "raid_leader_role";
    public static final String RaiderRole = "raider_role";
    public static final String SocialRole = "social_role";
    public static final String SignupChannel = "signup_channel";
    public static final String ArchiveChannel = "archive_channel";
    public static final String TankReaction = "tank_reaction";
    public static final String HealerReaction = "healer_reaction";
    public static final String MeleeReaction = "melee_reaction";
    public static final String RangedReaction = "ranged_reaction";
    public static final String CantAttendReaction = "cantattend_reaction";
    public static final String AcceptedEmote = "accepted_emote";
    public static final String BenchedEmote = "benched_emote";
    public static final String NoShowEmote = "noshow_emote";
    public static final String NotDecidedEmote = "notdecided_emote";

    public boolean saveServerSetting(String serverId, String settingName, String settingValue){
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

        if(!cache.containsKey(settingName)){
            cache.put(settingName, new HashMap<>());
        }
        cache.get(settingName).put(serverId, settingValue);

        return true;
    }

    public String loadServerSetting(String serverId, String settingName) {
        if(!cache.containsKey(settingName)){
            cache.put(settingName, new HashMap<>());
        }
        String value = cache.get(settingName).get(serverId);
        if(value != null){
            return value;
        }

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
