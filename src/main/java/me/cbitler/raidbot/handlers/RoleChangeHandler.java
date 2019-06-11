package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.PickFlexRoleStep;
import me.cbitler.raidbot.selection.PickRoleStep;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.http.impl.conn.SystemDefaultDnsResolver;

public class RoleChangeHandler extends ListenerAdapter {
	
	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		String guildID = event.getGuild().getId();
		List<Raid> raids = RaidManager.getRaids();
		
		for(Raid raid : raids){
			if(raid.getServerId().equals(guildID)){
				raid.updateMessage();
			}
		}
	}
    
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		Guild guild = event.getGuild();
		String guildID = guild.getId();
		
		List<Raid> raids = RaidManager.getRaids();
		
		for(Raid raid : raids){
			if(raid.getServerId().equals(guildID)){
				raid.removePossibleRaidersOnRoleChange(guild);
				raid.updateMessage();
			}
		}
	}
}
