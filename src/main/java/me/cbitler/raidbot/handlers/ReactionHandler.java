package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
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
				String emojiId = event.getReactionEmote().getEmote().getId();
				Reaction reaction = Reactions.getReactionFromEmojiId(emojiId);
				if (reaction != null) {
					if (raid.isUserInRaid(event.getUser().getId())) {
						raid.removeUser(event.getUser().getId());
					}

					raid.addUser(event.getUser().getId(), event.getUser().getName(), "", reaction.getSpec(), true, true);
				}
			}
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
