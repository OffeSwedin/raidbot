package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import me.cbitler.raidbot.utility.ServerSettings;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serves as a manager for all of the raids. This includes creating, loading,
 * and deleting raids
 * 
 * @author Christopher Bitler
 */
public class RaidManager {

	private static final List<Raid> raids = new ArrayList<>();

	/**
	 * Create a raid.
	 * 
	 * @param raidText
	 *          	The header-text for the raid
	 * @param serverId
	 * 				The id of the server to create the raid for
	 */
	public static void createRaid(String raidText, String serverId) {
		RaidBot bot = RaidBot.getInstance();
		Guild guild = bot.getServer(serverId);

		List<String> roles = new ArrayList<>();
		for(Reaction reaction : Reactions.getReactions()){
			roles.add(reaction.getSpec());
		}

		TextChannel channel = guild.getTextChannelsByName(ServerSettings.getInstance().getSignupChannel(serverId), true).get(0);
		Role role = guild.getRolesByName(ServerSettings.getInstance().getRaiderRole(serverId), true).get(0);

		MessageBuilder mb = new MessageBuilder();
		mb.setContent(role.getAsMention());

		try {
			mb.sendTo(channel).queue(message -> {

				boolean inserted = insertToDatabase(raidText, roles, message.getId(), message.getGuild().getId(),
						message.getChannel().getId());
				if (inserted) {
					Raid newRaid = new Raid(message.getId(), message.getGuild().getId(),
							message.getChannel().getId(), raidText);
					newRaid.addRoles(roles);
					raids.add(newRaid);

					for (Reaction reaction : Reactions.getReactions()) {
						message.addReaction(reaction.getEmote()).queue();
					}
					newRaid.updateMessage();
				} else {
					message.delete().queue();
				}
			});

		} catch (Exception e) {
			System.out.println("Error encountered in sending message.");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Create a raid.
	 *
	 * @param messageId
	 * 				The id of the raidmessage to edit
	 * @param raidText
	 *          	The header-text for the raid
	 */
	public static void editRaid(String messageId, String raidText){
		Raid raid = getRaid(messageId);

		if (raid != null) {
			try {
				RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `name` = ? WHERE `raidId` = ?",
						new String[] { raidText, messageId });
			} catch (Exception e) {
				System.out.println("Error encountered updating raid");
			}

			raid.editName(raidText);
			raid.updateMessage();
		}
	}

	/**
	 * Delete the raid from the database and maps, and delete the message if it
	 * is still there
	 *
	 * @param messageId
	 *            The raid ID
	 * @return true if deleted, false if not deleted
	 */
	public static boolean deleteRaid(String messageId) {
		Raid r = getRaid(messageId);
		if (r != null) {
			try {

				Guild guild = RaidBot.getInstance().getServer(r.getServerId());
				TextChannel archiveChannel = guild
						.getTextChannelsByName(ServerSettings.getInstance().getArchiveChannel(r.getServerId()), true).get(0);

				archiveChannel.sendMessage(r.buildEmbed()).queue(message1 -> {
				});

				RaidBot.getInstance().getServer(r.getServerId()).getTextChannelById(r.getChannelId())
						.getMessageById(messageId).queue(message -> message.delete().queue());

			} catch (Exception e) {
				System.out.println("Tried to delete raid without message");
				// Nothing, the message doesn't exist - it can happen
			}

			raids.removeIf((Raid raid) -> raid.getMessageId().equalsIgnoreCase(messageId));

			try {
				RaidBot.getInstance().getDatabase().update("DELETE FROM `raids` WHERE `raidId` = ?",
						new String[] { messageId });
				RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `raidId` = ?",
						new String[] { messageId });
			} catch (Exception e) {
				System.out.println("Error encountered deleting raid");
			}

			return true;
		}

		return false;
	}

	/**
	 * Insert a raid into the database
	 * 
	 * @param raidName
	 *            The name of the raid to insert
	 * @param raidRoles
	 *            The roles for the raid
	 * @param messageId
	 *            The embedded message / 'raidId'
	 * @param serverId
	 *            The serverId related to this raid
	 * @param channelId
	 *            The channelId for the announcement of this raid
	 * @return True if inserted, false otherwise
	 */
	private static boolean insertToDatabase(String raidName, List<String> raidRoles, String messageId, String serverId, String channelId) {
		RaidBot bot = RaidBot.getInstance();
		Database db = bot.getDatabase();

		String roles = formatRolesForDatabase(raidRoles);

		try {
			db.update(
					"INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `leader`, `name`, `description`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?,?,?)",
					new String[] { messageId, serverId, channelId, "", raidName,
							"", "", "", roles });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Load raids This first queries all of the raids and loads the raid data
	 * and adds the raids to the raid list Then, it queries the raid users and
	 * inserts them into their relevant raids, updating the embedded messages
	 */
	public static void loadRaids() {
		RaidBot bot = RaidBot.getInstance();
		Database db = bot.getDatabase();

		try {
			QueryResult results = db.query("SELECT * FROM `raids`", new String[] {});
			while (results.getResults().next()) {
				String name = results.getResults().getString("name");
				String rolesText = results.getResults().getString("roles");
				String messageId = results.getResults().getString("raidId");
				String serverId = results.getResults().getString("serverId");
				String channelId = results.getResults().getString("channelId");


				try {
					Raid raid = new Raid(messageId, serverId, channelId, name);
					ArrayList<String> roles = new ArrayList<>();
					String[] roleSplit = rolesText.split(";");
					Collections.addAll(roles, roleSplit);

					raid.addRoles(roles);
					raids.add(raid);
				} catch (Exception e) {
					System.out.println("Raid couldn't load: " + e.getMessage());
				}
			}
			results.getResults().close();
			results.getStmt().close();

			QueryResult userResults = db.query("SELECT * FROM `raidUsers`", new String[] {});

			while (userResults.getResults().next()) {
				String id = userResults.getResults().getString("userId");
				String name = userResults.getResults().getString("username");
				String spec = userResults.getResults().getString("spec");
				String role = userResults.getResults().getString("role");
				String raidId = userResults.getResults().getString("raidId");
				String signupStatus = userResults.getResults().getString("signupStatus");
				String signupTime = userResults.getResults().getString("signupTime");
				Timestamp timestamp = new Timestamp(Long.parseLong(signupTime));

				Raid raid = RaidManager.getRaid(raidId);
				if (raid != null) {
					RaidUser user = new RaidUser(id, name, spec, role, signupStatus, timestamp);
					raid.addUser(user, false, false);
				}
			}

			int delay = 0;
			for (Raid raid : raids) {
				raid.parseReactions();
				raid.updateMessage();
				delay = raid.resetReactions(delay);
			}
		} catch (SQLException e) {
			System.out.println("Couldn't load raids.. exiting");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Get a raid from the discord message ID
	 * 
	 * @param messageId
	 *            The discord message ID associated with the raid's embedded
	 *            message
	 * @return The raid object related to that messageId, if it exist.
	 */
	public static Raid getRaid(String messageId) {
		for (Raid raid : raids) {
			if (raid.getMessageId().equalsIgnoreCase(messageId)) {
				return raid;
			}
		}
		return null;
	}

	/**
	 * Formats the roles associated with a raid in a form that can be inserted
	 * into a database row. This combines them as
	 * [number]:[name];[number]:[name];...
	 * 
	 * @param rolesWithNumbers
	 *            The roles and their amounts
	 * @return The formatted string
	 */
	private static String formatRolesForDatabase(List<String> rolesWithNumbers) {
		StringBuilder data = new StringBuilder();

		for (int i = 0; i < rolesWithNumbers.size(); i++) {
			String role = rolesWithNumbers.get(i);
			if (i == rolesWithNumbers.size() - 1) {
				data.append(role);
			} else {
				data.append(role).append(";");
			}
		}

		return data.toString();
	}

	public static List<Raid> getRaids() {
		return raids;
	}
}
