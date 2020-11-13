package mintey.raidbot.handlers;

import mintey.raidbot.raids.Raid;
import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.raids.RaidUser;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NicknameChangeHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(NicknameChangeHandler.class);

	@Override
	public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
		try{
			String guildID = event.getGuild().getId();
			List<Raid> raids = RaidManager.getRaids();

			log.info("Parsing namechange from " + event.getOldNickname() +
					" to " + event.getMember().getEffectiveName());

			for(Raid raid : raids){
				if(raid.serverId.equals(guildID)){
					RaidUser user = raid.getRaidUser(event.getUser().getId());
					if(user != null){
						user.name = event.getMember().getEffectiveName();
						user.save();
					}
					raid.updateMessage();
				}
			}
		} catch(Exception bigError){
			log.error("Something went wrong when parsing message. ", bigError);
		}
	}
}
