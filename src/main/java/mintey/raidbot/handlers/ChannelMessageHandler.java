package mintey.raidbot.handlers;

import mintey.raidbot.commands.Command;
import mintey.raidbot.commands.CommandRegistry;
import mintey.raidbot.raids.RaidManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelMessageHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(ChannelMessageHandler.class);

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		try{
			if (event.getAuthor().isBot()) {
				return;
			}

			if (event.getMessage().getContentRaw().startsWith("!")) {

				if(event.getMember() != null){
					log.info("Parsing channel command message event from " + event.getMember().getEffectiveName());
				}

				String[] messageParts = event.getMessage().getContentRaw().split(" ");
				String[] arguments = CommandRegistry.getArguments(messageParts);

				Command command = CommandRegistry.getCommand(messageParts[0].replace("!", ""));
				if (command != null) {
					command.handleCommand(arguments, event.getChannel(), event.getAuthor());

					try {
						event.getMessage().delete().queue();
					} catch (Exception exception) {
						event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel
								.sendMessage("Make sure that the bot has the 'Manage message' permission").queue());
						log.error("Failed when deleting channel message. ", event);
					}
				}
			}
		} catch(Exception bigError){
			log.error("Something went wrong when pasring message. ", bigError);
		}
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent e) {
		try{
			if (RaidManager.getRaid(e.getMessageId()) != null) {
				RaidManager.deleteRaid(e.getMessageId());
			}
		} catch(Exception bigError){
			log.error("Something went wrong when parsing message. ", bigError);
		}
	}
}
