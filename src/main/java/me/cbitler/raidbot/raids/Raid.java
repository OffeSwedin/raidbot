package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Represents a raid and has methods for adding/removing users,
 * etc
 */
public class Raid {
	private String messageId, name, date, time, serverId, channelId;
	private List<String> roles = new ArrayList<>();
	private List<RaidUser> users = new ArrayList<>();

	/**
	 * Create a new Raid with the specified data
	 * 
	 * @param messageId
	 *            The embedded message Id related to this raid
	 * @param serverId
	 *            The serverId that the raid is on
	 * @param channelId
	 *            The announcement channel's id for this raid
	 * @param name
	 *            The name of the raid
	 * @param date
	 *            The date of the raid
	 * @param time
	 *            The time of the raid
	 */
	public Raid(String messageId, String serverId, String channelId, String name, String date, String time) {
		this.messageId = messageId;
		this.serverId = serverId;
		this.channelId = channelId;
		this.name = name;
		this.date = date;
		this.time = time;
	}

	/**
	 * Get the message ID for this raid
	 * 
	 * @return The message ID for this raid
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Get the server ID for this raid
	 * 
	 * @return The server ID for this raid
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * Get the channel ID for this raid
	 * 
	 * @return The channel ID for this raid
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * Add a user to this raid. This first creates the user and attempts to
	 * insert it into the database, if needed Then it adds them to list of raid
	 * users with their role
	 * 
	 * @param user
	 *            The user
	 * @param db_insert
	 *            Whether or not the user should be inserted. This is false when
	 *            the roles are loaded from the database.
	 */
	public void addUser(RaidUser user, boolean db_insert,
			boolean update_message) {

		if (db_insert) {
			try {
				RaidBot.getInstance().getDatabase()
						.update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`, `signupStatus`)"
								+ " VALUES (?,?,?,?,?,?)", new String[] { user.id, user.name, user.spec, user.role, this.messageId, user.signupStatus });
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		users.add(user);

		if (update_message) {
			updateMessage();
		}
	}

	public void acceptUser(RaidUser userToAccept){
		for(RaidUser user : users){
			if(user.id.equalsIgnoreCase(userToAccept.id)){
				user.accept();
			}
		}
	}

	public void benchUser(RaidUser userToBench){
		for(RaidUser user : users){
			if(user.id.equalsIgnoreCase(userToBench.id)){
				user.bench();
			}
		}
	}

	public void addRoles(List<String> roles){
		this.roles.addAll(roles);
	}

	/**
	 * Check if a specific user is in this raid
	 * 
	 * @param id
	 *            The id of the user
	 * @return True if they are in the raid, false otherwise
	 */
	public boolean isUserInRaid(String id) {
		for (RaidUser user : users){
			if(user.id.equalsIgnoreCase(id)){
				return true;
			}
		}

		return false;
	}

	/**
	 * Remove a user from this raid. This also updates the database to remove
	 * them from the raid
	 * 
	 * @param id
	 *            The user's id
	 */
	public void removeUser(String id) {
		users.removeIf((RaidUser user) -> user.id.equalsIgnoreCase(id));

		try {
			RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
					new String[] { id, getMessageId() });
		} catch (SQLException e) {
			e.printStackTrace();
		}

		updateMessage();
	}

	/**
	 * Send the dps report log links to the players in this raid
	 * 
	 * @param logLinks
	 *            The list of links
	 */
	public void messagePlayersWithLogLinks(List<String> logLinks) {
		String logLinkMessage = "ArcDPS reports from **" + this.name + "**:\n";
		for (String link : logLinks) {
			logLinkMessage += (link + "\n");
		}

		final String finalLogLinkMessage = logLinkMessage;
		for (RaidUser user : this.users) {
			RaidBot.getInstance().getServer(this.serverId).getMemberById(user.id).getUser().openPrivateChannel()
					.queue(privateChannel -> privateChannel.sendMessage(finalLogLinkMessage).queue());
		}
	}

	public void removePossibleRaidersOnRoleChange(Guild guild){
		List<Member> members = guild.getMembers();
		
		for (Member member : members) {
			if(this.isUserInRaid(member.getUser().getId()) && !PermissionsUtil.hasRaiderRole(member)) {
				this.removeUser(member.getUser().getId());
			}
		}
	}
		
	/**
	 * Update the embedded message for the raid
	 */
	public void updateMessage() {
		
		MessageEmbed embed = buildEmbed();
		try {
			RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId())
					.editMessageById(getMessageId(), embed).queue();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Build the embedded message that shows the information about this raid
	 * 
	 * @return The embedded message representing this raid
	 */
	public MessageEmbed buildEmbed() {
		RaidBot bot = RaidBot.getInstance();
		Guild guild = bot.getJda().getGuilds().get(0);

		List<Member> notRespondedMembers = getNotRespondedMembers(guild);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("**" + this.name + " - " + this.date + " " + this.time + "**");
		builder.addBlankField(false);
		builder.addField("Roles:", buildRolesText(guild), true);
		builder.addField("Not responded: ("+notRespondedMembers.size()+")", buildNotRespondedText(notRespondedMembers) , true);
		builder.addBlankField(false);
		builder.addField("ID: ", messageId, true);

		return builder.build();
	}

	private List<Member> getNotRespondedMembers(Guild guild) {
		List<Member> members = guild.getMembers();
		List<Member> notRespondedMembers = new ArrayList<>();

		for (Member member : members) {
			if (!this.isUserInRaid(member.getUser().getId())) {
				if (PermissionsUtil.hasRaiderRole(member)) {
					notRespondedMembers.add(member);
				}
			}
		}
		return notRespondedMembers;
	}

	/**
	 * Build the not responded text
	 * 
	 * @return The not responded text
	 */
	private String buildNotRespondedText(List<Member> notRespondedMembers) {
		String response = "";
		
		for (Member member : notRespondedMembers) {
			response += member.getEffectiveName() + "\n";
		}

		return response;
	}

	/**
	 * Build the role text, which shows the roles users are playing in the raids
	 * 
	 * @return The role text
	 */
	private String buildRolesText(Guild guild) {
		String text = "";
		for (String role : roles) {
			List<RaidUser> usersInRole = getUsersInRole(role);
			text += ("**" + role + " (" + usersInRole.size() + "):** \n");
			for (RaidUser user : usersInRole) {
				
				text += "   - " + guild.getMemberById(user.id).getEffectiveName() + createSignupStatusText(user) + "\n";
				
				
			}
			text += "\n";
		}
		return text;
	}

	private String createSignupStatusText(RaidUser user){
		if(user.isAccepted()){
			return "(Y)";
		}else if(user.isBenched()){
			return "(B)";
		}else{
			return "(?)";
		}
	}
	
	/**
	 * Get list of users in a role
	 *
	 * @param role
	 *            The name of the role
	 * @return The users in the role
	 */
	private List<RaidUser> getUsersInRole(String role) {
		List<RaidUser> usersInRole = new ArrayList<>();

		for (RaidUser user : users){
			if (user.role.equalsIgnoreCase(role)){
				usersInRole.add(user);
			}
		}

		return usersInRole;
	}

	/**
	 * Get a RaidUser in this raid by their name
	 * 
	 * @param name
	 *            The user's name
	 * @return The RaidUser if they are in this raid, null otherwise
	 */
	public RaidUser getUserByName(String name) {
		for (RaidUser user : users) {
			if (user.name.equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	public void resetReactions(){
		try {
			RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId()).clearReactionsById(getMessageId()).queue();
			for (Reaction reaction : Reactions.getReactions()){
				RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId()).addReactionById(getMessageId(), reaction.getEmote()).queue();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
