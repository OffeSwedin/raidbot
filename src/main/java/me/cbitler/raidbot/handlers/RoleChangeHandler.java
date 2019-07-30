package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoleChangeHandler extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(RoleChangeHandler.class);

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		try {
			String guildID = event.getGuild().getId();
			List<Raid> raids = RaidManager.getRaids();

			log.info("Parsing role add from " + event.getMember().getEffectiveName());

			for (Raid raid : raids) {
				if (raid.serverId.equals(guildID)) {
					raid.updateMessage();
				}
			}
		} catch (Exception bigError) {
			log.error("Something went wrong when parsing message. ", bigError);
		}
	}

	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		try {
			Guild guild = event.getGuild();

			log.info("Parsing role remove from " + event.getMember().getEffectiveName());

			Boolean isNotAllowedToRaid = !PermissionsUtil.isAllowedToRaid(event.getMember());

			for (Raid raid : RaidManager.getRaids()) {
				if (raid.serverId.equals(guild.getId())) {
					if (isNotAllowedToRaid) {
						raid.removeUser(event.getUser().getId());
					}
					raid.updateMessage();
				}
			}

		} catch (Exception bigError) {
			log.error("Something went wrong when pasring message. ", bigError);
		}
	}
}
