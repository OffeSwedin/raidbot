package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.commands.Command;
import me.cbitler.raidbot.commands.CommandRegistry;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Handle channel message-related events sent to the bot
 * 
 * @author Christopher Bitler
 */
public class ChannelMessageHandler extends ListenerAdapter {

	/**
	 * Handle receiving a message. This checks to see if it matches the
	 * !createRaid or !removeFromRaid commands and acts on them accordingly.
	 *
	 * @param e
	 *            The event
	 */
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		RaidBot bot = RaidBot.getInstance();
		if (e.getAuthor().isBot()) {
			return;
		}

		if (e.getMessage().getContentRaw().startsWith("!")) {
			String[] messageParts = e.getMessage().getContentRaw().split(" ");
			String[] arguments = CommandRegistry.getArguments(messageParts);
			Command command = CommandRegistry.getCommand(messageParts[0].replace("!", ""));
			if (command != null) {
				command.handleCommand(messageParts[0], arguments, e.getChannel(), e.getAuthor());

				try {
					e.getMessage().delete().queue();
				} catch (Exception exception) {
					e.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Make sure that the bot has the 'Manage message' permission").queue());
				}
			}
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
		if (RaidManager.getRaid(e.getMessageId()) != null) {
			RaidManager.deleteRaid(e.getMessageId());
		}
	}

	/**
	 * Combine the strings in an array of strings
	 * 
	 * @param parts
	 *            The array of strings
	 * @param offset
	 *            The offset in the array to start at
	 * @return The combined string
	 */
	private String combineArguments(String[] parts, int offset) {
		String text = "";
		for (int i = offset; i < parts.length; i++) {
			text += (parts[i] + " ");
		}

		return text.trim();
	}
}
