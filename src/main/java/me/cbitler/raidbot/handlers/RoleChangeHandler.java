package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

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
