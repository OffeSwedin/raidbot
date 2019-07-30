package me.cbitler.raidbot.utility;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * Utility class for handling permissions
 * 
 * @author Christopher Bitler
 */
public class PermissionsUtil {
	public static boolean isAllowedToRaid(Member member){
		return isRaider(member) || isSocial(member);
	}

	public static boolean isRaidLeader(Member member) {
		String raidLeaderRole = ServerSettings.getInstance().getRaidLeaderRole(member.getGuild().getId());
		return hasRole(member, raidLeaderRole);
	}

	public static boolean isRaider(Member member) {
		String raiderRole = ServerSettings.getInstance().getRaiderRole(member.getGuild().getId());
		return hasRole(member, raiderRole);
	}

	public static boolean isSocial(Member member) {
		String socialRole = ServerSettings.getInstance().getSocialRole(member.getGuild().getId());
		return hasRole(member, socialRole);
	}

	private static boolean hasRole(Member member, String roleName) {
		for (Role role : member.getRoles()) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}
}
