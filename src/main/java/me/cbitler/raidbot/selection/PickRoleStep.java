package me.cbitler.raidbot.selection;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for picking a role for a raid
 * @author Christopher Bitler
 */
public class PickRoleStep implements SelectionStep {
    Raid raid;
    String spec;

    /**
     * Create a new step for this role selection with the specified raid and spec
     * that the user chose
     * @param raid The raid
     * @param spec The specialization that the user chose
     */
    public PickRoleStep(Raid raid, String spec) {
        this.raid = raid;
        this.spec = spec;
    }

    /**
     * Handle the user input - checks to see if the role they are picking is valid
     * and not full, and if so, adding them to that role
     * @param e The private message event
     * @return True if the user chose a valid, not full, role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        if(raid.isValidNotFullRole(e.getMessage().getContentRaw())) {
            raid.addUser(e.getAuthor().getId(), e.getAuthor().getName(), spec, e.getMessage().getContentRaw(), true, true);
            e.getChannel().sendMessage("Added to raid roster").queue();
            return true;
        } else {
            e.getChannel().sendMessage("Please choose a valid role that is not full.").queue();
            return false;
        }
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    @Override
    public SelectionStep getNextStep() {
        return null;
    }

    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        String text = "Pick a role (";
        for (int i = 0; i < raid.getRoles().size(); i++) {
            if (i == raid.getRoles().size()-1) {
                text += raid.getRoles().get(i).getName();
            } else {
                text += (raid.getRoles().get(i).getName() + ", ");
            }
        }
        text += ") or type cancel to cancel role selection.";

        return text;
    }
}
