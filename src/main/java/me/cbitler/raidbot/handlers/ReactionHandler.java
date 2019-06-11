package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.PickFlexRoleStep;
import me.cbitler.raidbot.selection.PickRoleStep;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Consumer;

import org.apache.http.impl.conn.SystemDefaultDnsResolver;

public class ReactionHandler extends ListenerAdapter {
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		Raid raid = RaidManager.getRaid(event.getMessageId());
		if (event.getUser().isBot()) {
			return;
		}

		if (raid != null) {

			if (PermissionsUtil.hasRaiderRole(event.getMember())) {
				// Checks for the "command" emojis
				String emojiName = event.getReactionEmote().getEmote().getName();
				if (Reactions.getSpecs().contains(emojiName)) {
					RaidBot bot = RaidBot.getInstance();
					if (raid.isUserInRaid(event.getUser().getId())) {
						raid.removeUser(event.getUser().getId());
					}

					raid.addUser(event.getUser().getId(), event.getUser().getName(), "test", emojiName, true, true);
				}
			}
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
