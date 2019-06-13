package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Set the time for the raid
 * @author Christopher Bitler
 */
public class RunTimeStep implements CreationStep {

    /**
     * Handle setting the time for the raid
     * @param e The direct message event
     * @return True if the time is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if (raid == null) {
            return false;
        }

        raid.setTime(e.getMessage().getContentRaw());

        int aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis = 999;
        
        raid.getRolesWithNumbers().add(new RaidRole(aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis, "Tank"));
        raid.getRolesWithNumbers().add(new RaidRole(aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis, "Healer"));
        raid.getRolesWithNumbers().add(new RaidRole(aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis, "Melee"));
        raid.getRolesWithNumbers().add(new RaidRole(aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis, "Ranged"));
        raid.getRolesWithNumbers().add(new RaidRole(aNumberThatIsSoLargeWeWillNeverNeedLargerThanTHis, "NotAttending"));
        
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the time for raid run:";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
    	return null;
    }
}
