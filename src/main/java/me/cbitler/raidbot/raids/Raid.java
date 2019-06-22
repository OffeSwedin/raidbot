package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.utility.EnvVariables;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import me.cbitler.raidbot.utility.TimestampComparator;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
						.update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`, `signupStatus`, `signupTime`)"
								+ " VALUES (?,?,?,?,?,?,?)", new String[] { user.id, user.name, user.spec, user.role, this.messageId, user.signupStatus, Long.toString(user.signupTime.getTime())});
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		users.add(user);

		if (update_message) {
			updateMessage();
		}
	}

	public boolean acceptUser(String username){
		for(RaidUser user : users){
			if(user.name.equalsIgnoreCase(username)){
				user.accept();
				updateUser(user);
				return true;
			}
		}
		return false;
	}

	public boolean benchUser(String username){
		for(RaidUser user : users){
			if(user.name.equalsIgnoreCase(username)){
				user.bench();
				updateUser(user);
				return true;
			}
		}
		return false;
	}

	private void updateUser(RaidUser user) {
		try {
			RaidBot.getInstance().getDatabase()
					.update("UPDATE `raidUsers` SET `signupStatus` = ? WHERE `userId` = ? AND `raidId` = ?",
							new String[] { user.signupStatus, user.id, this.messageId});
		} catch (SQLException e) {
			e.printStackTrace();
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
			MessageBuilder mb = new MessageBuilder();
			mb.setEmbed(embed);
			mb.setContent(" ");
			Message message = mb.build();
			RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId())
					.editMessageById(getMessageId(), message).queue();
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
		
		List<List<String>> roleTexts = buildRolesText(guild);
		System.err.println(roleTexts.toString());
		int uglyLoopCounterIndex = 0;
		for(List<String> role: roleTexts){

			String header = role.get(0);
			role.remove(0);
			
			String fullRoleText = "";
			for(String roleText : role){
				fullRoleText += roleText + "\n";
				System.err.println(roleText);
			}
			builder.addField(header, fullRoleText, true);
			uglyLoopCounterIndex++;
			if(uglyLoopCounterIndex == 3){
				builder.addBlankField(false);
			}
		}
		
		builder.addField("**Not responded: ("+notRespondedMembers.size()+")**", buildNotRespondedText(notRespondedMembers) , true);
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
			response += member.getAsMention() + "\n";
		}

		return response;
	}

	/**
	 * Build the role text, which shows the roles users are playing in the raids
	 * 
	 * @return The role text
	 */
	private List<List<String>> buildRolesText(Guild guild) {
		List<List<String>> roleTexts = new ArrayList<List<String>>();

		for (String role : roles) {
			ArrayList<String> roleStrings = new ArrayList<String>();
			roleTexts.add(roleStrings);
			
			List<RaidUser> usersInRole = getUsersInRole(role);
			usersInRole.sort(new TimestampComparator());
			
			roleStrings.add("**" + role + " (" + usersInRole.size() + "):**");
			
			for (RaidUser user : usersInRole) {
				if(!role.equals("Not Attending")){
					roleStrings.add("   - "+ guild.getMemberById(user.id).getEffectiveName() + " " + createSignupStatusText(user));
				}else{
					roleStrings.add("   - "+ guild.getMemberById(user.id).getEffectiveName());
				}
					
			}
		}
		return roleTexts;
	}

	private String createSignupStatusText(RaidUser user){
		Emote emote;
		if(user.isAccepted()){
			if(EnvVariables.getInstance().isTestEnvironment()){
				emote = RaidBot.getInstance().getJda().getEmoteById("590293224250408981");
			}else{
				emote = RaidBot.getInstance().getJda().getEmoteById("590293759166775307");
			}
		}else if(user.isBenched()){
			if(EnvVariables.getInstance().isTestEnvironment()) {
				emote = RaidBot.getInstance().getJda().getEmoteById("590292731436335166");
			}else{
				emote = RaidBot.getInstance().getJda().getEmoteById("590293760726925372");
			}
		}else{
			if(EnvVariables.getInstance().isTestEnvironment()) {
				emote = RaidBot.getInstance().getJda().getEmoteById("590292700842950656");
			}else{
				emote = RaidBot.getInstance().getJda().getEmoteById("590293759519096832");
			}
		}

		return "<:" + emote.getName() + ":" + emote.getId() + ">";
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

	public void parseReactions() {
		Guild guild = RaidBot.getInstance().getJda().getGuilds().get(0);
		Message message = RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId()).getMessageById(getMessageId()).complete();
		for(MessageReaction reaction : message.getReactions()){
			List<User> users = reaction.getUsers().complete();
			for(User user : users){
				if(!user.isBot()){
					parseReaction(guild.getMember(user), reaction.getReactionEmote().getEmote(), false);
				}
			}
		}
	}

	public void parseReaction(Member member, Emote emote, boolean update_message){
		if (PermissionsUtil.hasRaiderRole(member)) {

			String emojiId = emote.getId();
			Reaction reaction = Reactions.getReactionFromEmojiId(emojiId);
			if (reaction != null) {
				if (this.isUserInRaid(member.getUser().getId())) {
					this.removeUser(member.getUser().getId());
				}

				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				RaidUser user = new RaidUser(member.getUser().getId(), member.getEffectiveName(), "", reaction.getSpec(), timestamp);
				this.addUser(user, true, update_message);
			}
		}
	}

	public int resetReactions(int delay){
		try {
			RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId()).clearReactionsById(getMessageId()).queueAfter(delay, TimeUnit.MILLISECONDS);
			delay += 1000;
			for (Reaction reaction : Reactions.getReactions()){
				RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId()).addReactionById(getMessageId(), reaction.getEmote()).queueAfter(delay, TimeUnit.MILLISECONDS);
				delay += 1000;
				System.err.println("Delay: " + delay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return delay;
	}
}
