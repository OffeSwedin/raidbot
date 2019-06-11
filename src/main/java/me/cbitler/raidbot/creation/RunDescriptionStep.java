package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Set the description for the raid
 * @author Christopher Bitler
 */
public class RunDescriptionStep implements CreationStep {

    /**
     * Set the decsription for the raid
     * @param e The direct message event
     * @return True always
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if (raid == null) {
            return false;
        }

        raid.setDescription(e.getMessage().getContentRaw());
        
        System.err.println("Value of signup channel: " + bot.getSignupChannel(raid.getServerId()));
        
        raid.setAnnouncementChannel(bot.getSignupChannel(raid.getServerId()));

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter description for raid run:";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return new RunDateStep();
    }
}
