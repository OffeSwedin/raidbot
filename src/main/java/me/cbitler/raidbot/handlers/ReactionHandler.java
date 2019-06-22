package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionHandler extends ListenerAdapter {
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		Raid raid = RaidManager.getRaid(event.getMessageId());
		if (event.getUser().isBot()) {
			return;
		}

		if (raid != null) {
			raid.parseReaction(event.getMember(), event.getReactionEmote().getEmote(), true);
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}
}
