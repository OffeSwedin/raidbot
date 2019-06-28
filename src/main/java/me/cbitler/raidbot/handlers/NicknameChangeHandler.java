package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NicknameChangeHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(NicknameChangeHandler.class);

	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
		String guildID = event.getGuild().getId();
		List<Raid> raids = RaidManager.getRaids();

		log.info("Parsing namechange from " + event.getPrevNick() +
				" to " + event.getMember().getEffectiveName());

		for(Raid raid : raids){
			if(raid.getServerId().equals(guildID)){
				raid.updateUserNickname(event.getUser().getId(), event.getMember().getEffectiveName());
				raid.updateMessage();
			}
		}
	}
}
