package me.cbitler.raidbot.raids;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store data about a raid that is being set up
 * This isn't commented as the method names should be self-explanatory
 * @author Christopher Bitler
 */
public class PendingRaid {
    private String name, description, announcementChannel, serverId, leaderName;
    private List<String> roles = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnouncementChannel() {
        return announcementChannel;
    }

    public void setAnnouncementChannel(String announcementChannel) {
        this.announcementChannel = announcementChannel;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public List<String> getRoles() { return roles; }

    public void addRole(String role) { roles.add(role); }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
