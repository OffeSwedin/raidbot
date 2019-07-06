package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NicknameChangeHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(NicknameChangeHandler.class);

	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
		try{
			String guildID = event.getGuild().getId();
			List<Raid> raids = RaidManager.getRaids();

			log.info("Parsing namechange from " + event.getPrevNick() +
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
