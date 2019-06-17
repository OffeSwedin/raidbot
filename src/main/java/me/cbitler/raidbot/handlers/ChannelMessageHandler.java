package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.commands.Command;
import me.cbitler.raidbot.commands.CommandRegistry;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation.RunNameStep;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidUser;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.Permission;
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

		if (e.getMember().getPermissions().contains(Permission.MANAGE_SERVER)) {
			if (e.getMessage().getContentRaw().toLowerCase().startsWith("!setraidleaderrole")) {

			} else if (e.getMessage().getContentRaw().toLowerCase().startsWith("!setraiderrole")) {
				try {
					String[] commandParts = e.getMessage().getContentRaw().split(" ");
					String raiderRole = combineArguments(commandParts, 1);
					RaidBot.getInstance().setRaiderRole(e.getMember().getGuild().getId(), raiderRole);
					e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Raider role updated to: " + raiderRole).queue());
					e.getMessage().delete().queue();
				} catch (Exception exc) {
					e.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
				}
			} else if (e.getMessage().getContentRaw().toLowerCase().startsWith("!setsignupchannel")) {
				try {
					
					System.err.println("received command");
					
					String[] commandParts = e.getMessage().getContentRaw().split(" ");
					String signupChannel = combineArguments(commandParts, 1);
					
					RaidBot.getInstance().setSignupChannel(e.getMember().getGuild().getId(), signupChannel);
					
					e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Signup channel updated to: " + signupChannel).queue());
					e.getMessage().delete().queue();
				} catch (Exception exc) {
					e.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
				}
			}  else if (e.getMessage().getContentRaw().toLowerCase().startsWith("!setarchivechannel")) {
				try {
					
					String[] commandParts = e.getMessage().getContentRaw().split(" ");
					String archiveChannel = combineArguments(commandParts, 1);
					
					RaidBot.getInstance().setArchiveChannel(e.getMember().getGuild().getId(), archiveChannel);
					
					e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Archive channel updated to: " + archiveChannel).queue());
					e.getMessage().delete().queue();
				} catch (Exception exc) {
					e.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
							.sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
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
