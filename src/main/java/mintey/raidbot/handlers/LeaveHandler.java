package mintey.raidbot.handlers;

import mintey.raidbot.raids.Raid;
import mintey.raidbot.raids.RaidManager;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LeaveHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(LeaveHandler.class);

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
		try{
			String guildID = event.getGuild().getId();
			List<Raid> raids = RaidManager.getRaids();

			log.info("Parsing leave server event from " + event.getUser().getName());

			for(Raid raid : raids){
				if(raid.serverId.equals(guildID)){
					raid.removeUser(event.getUser().getId());
					raid.updateMessage();
				}
			}
		} catch(Exception bigError){
			log.error("Something went wrong when parsing message. ", bigError);
		}
	}
}
