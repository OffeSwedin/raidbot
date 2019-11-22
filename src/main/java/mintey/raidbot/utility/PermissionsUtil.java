package mintey.raidbot.utility;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
public class PermissionsUtil {
	public static boolean isAllowedToRaid(Member member){
		return isRaider(member) || isSocial(member);
	}

	public static boolean isRaidLeader(Member member) {
		String raidLeaderRole = ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaidLeaderRole);
		return hasRole(member, raidLeaderRole);
	}

	public static boolean isRaider(Member member) {
		String raiderRole = ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.RaiderRole);
		return hasRole(member, raiderRole);
	}

	public static boolean isSocial(Member member) {
		String socialRole = ServerSettings.getInstance().loadServerSetting(member.getGuild().getId(), ServerSettings.SocialRole);
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
