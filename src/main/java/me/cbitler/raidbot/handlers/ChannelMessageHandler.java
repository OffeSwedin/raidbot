package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.commands.Command;
import me.cbitler.raidbot.commands.CommandRegistry;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle channel message-related events sent to the bot
 * 
 * @author Christopher Bitler
 */
public class ChannelMessageHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(ChannelMessageHandler.class);

	/**
	 * Handle receiving a message. This checks to see if it matches the
	 * !createRaid or !removeFromRaid commands and acts on them accordingly.
	 *
	 * @param e
	 *            The event
	 */
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		try{
			if (e.getAuthor().isBot()) {
				return;
			}

			if (e.getMessage().getContentRaw().startsWith("!")) {
				String[] messageParts = e.getMessage().getContentRaw().split(" ");
				String[] arguments = CommandRegistry.getArguments(messageParts);

				Command command = CommandRegistry.getCommand(messageParts[0].replace("!", ""));
				if (command != null) {
					command.handleCommand(arguments, e.getChannel(), e.getAuthor());

					try {
						e.getMessage().delete().queue();
					} catch (Exception exception) {
						e.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
								.sendMessage("Make sure that the bot has the 'Manage message' permission").queue());
						log.error("Failed with SQL query. ", e);
					}
				}
			}
		} catch(Exception bigError){
			log.error("Something went wrong when pasring message. ", bigError);
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
		try{
			if (RaidManager.getRaid(e.getMessageId()) != null) {
				RaidManager.deleteRaid(e.getMessageId());
			}
		} catch(Exception bigError){
			log.error("Something went wrong when parsing message. ", bigError);
		}
	}
}
