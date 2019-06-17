package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Handle direct messages sent to the bot
 * @author Christopher Bitler
 */
public class DMHandler extends ListenerAdapter {

    /**
     * Handle receiving a private message.
     * This checks to see if the user is currently going through the raid creation process or
     * the role selection process and acts accordingly.
     * @param e The private message event
     */
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        User author = e.getAuthor();

        if (bot.getCreationMap().containsKey(author.getId())) {
            if(e.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {
                bot.getCreationMap().remove(author.getId());

                if(bot.getPendingRaids().get(author.getId()) != null) {
                    bot.getPendingRaids().remove(author.getId());
                }
                e.getChannel().sendMessage("Raid creation cancelled.").queue();
                return;

            }

            CreationStep step = bot.getCreationMap().get(author.getId());
            boolean done = step.handleDM(e);

            // If this step is done, move onto the next one or finish
            if (done) {
                CreationStep nextStep = step.getNextStep();
                if(nextStep != null) {
                    bot.getCreationMap().put(author.getId(), nextStep);
                    e.getChannel().sendMessage(nextStep.getStepText()).queue();
                } else {
                    //Create raid
                    bot.getCreationMap().remove(author.getId());
                    PendingRaid raid = bot.getPendingRaids().remove(author.getId());
                    try {
                        RaidManager.createRaid(raid);
                        e.getChannel().sendMessage("Raid Created").queue();
                    } catch (Exception exception) {
                    	System.err.println(exception.getMessage());
                        e.getChannel().sendMessage("Cannot create raid - does the bot have permission to post in the specified channel?").queue();
                    }
                }
            }
        }
    }
}
