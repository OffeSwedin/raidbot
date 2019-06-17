package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serves as a manager for all of the raids. This includes creating, loading,
 * and deleting raids
 * 
 * @author Christopher Bitler
 */
public class RaidManager {

	private static List<Raid> raids = new ArrayList<>();

	/**
	 * Create a raid. This turns a PendingRaid object into a Raid object and
	 * inserts it into the list of raids. It also sends the associated embedded
	 * message and adds the reactions for people to join to the embed
	 * 
	 * @param raid
	 *            The pending raid to create
	 */
	public static void createRaid(PendingRaid raid) {
		MessageEmbed message = buildEmbed(raid);

		Guild guild = RaidBot.getInstance().getServer(raid.getServerId());
		List<TextChannel> channels = guild.getTextChannelsByName(raid.getAnnouncementChannel(), true);
		if (channels.size() > 0) {
			// We always go with the first channel if there is more than one
			try {
				channels.get(0).sendMessage(message).queue(message1 -> {

					boolean inserted = insertToDatabase(raid, message1.getId(), message1.getGuild().getId(),
							message1.getChannel().getId());
					if (inserted) {
						Raid newRaid = new Raid(message1.getId(), message1.getGuild().getId(),
								message1.getChannel().getId(), raid.getName(), raid.getDate(), raid.getTime());
						newRaid.addRoles(raid.getRolesWithNumbers());
						raids.add(newRaid);

						for (Reaction reaction : Reactions.getReactions()) {
							message1.addReaction(reaction.getEmote()).queue();
						}
						newRaid.updateMessage();
					} else {
						message1.delete().queue();
					}
				});

			} catch (Exception e) {
				System.out.println("Error encountered in sending message.");
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * Insert a raid into the database
	 * 
	 * @param raid
	 *            The raid to insert
	 * @param messageId
	 *            The embedded message / 'raidId'
	 * @param serverId
	 *            The serverId related to this raid
	 * @param channelId
	 *            The channelId for the announcement of this raid
	 * @return True if inserted, false otherwise
	 */
	private static boolean insertToDatabase(PendingRaid raid, String messageId, String serverId, String channelId) {
		RaidBot bot = RaidBot.getInstance();
		Database db = bot.getDatabase();

		String roles = formatRolesForDatabase(raid.getRolesWithNumbers());

		try {
			db.update(
					"INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `leader`, `name`, `description`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?,?,?)",
					new String[] { messageId, serverId, channelId, raid.getLeaderName(), raid.getName(),
							raid.getDescription(), raid.getDate(), raid.getTime(), roles });
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
				String date = results.getResults().getString("date");
				String time = results.getResults().getString("time");
				String rolesText = results.getResults().getString("roles");
				String messageId = results.getResults().getString("raidId");
				String serverId = results.getResults().getString("serverId");
				String channelId = results.getResults().getString("channelId");


				try {
					Raid raid = new Raid(messageId, serverId, channelId, name, date, time);
					ArrayList<RaidRole> roles = new ArrayList<>();
					String[] roleSplit = rolesText.split(";");
					for (String roleAndAmount : roleSplit) {
						String[] parts = roleAndAmount.split(":");
						int amnt = Integer.parseInt(parts[0]);
						String role = parts[1];
						roles.add(new RaidRole(amnt, role));
					}
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

				Raid raid = RaidManager.getRaid(raidId);
				if (raid != null) {
					RaidUser user = new RaidUser(id, name, spec, role);
					raid.addUser(user, false, false);
				}
			}

			for (Raid raid : raids) {
				raid.updateMessage();
				raid.resetReactions();
			}
		} catch (SQLException e) {
			System.out.println("Couldn't load raids.. exiting");
			e.printStackTrace();
			System.exit(1);
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
						.getTextChannelsByName(RaidBot.getInstance().getArchiveChannel(r.getServerId()), true).get(0);

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
	private static String formatRolesForDatabase(List<RaidRole> rolesWithNumbers) {
		String data = "";

		for (int i = 0; i < rolesWithNumbers.size(); i++) {
			RaidRole role = rolesWithNumbers.get(i);
			if (i == rolesWithNumbers.size() - 1) {
				data += (role.amount + ":" + role.name);
			} else {
				data += (role.amount + ":" + role.name + ";");
			}
		}

		return data;
	}

	/**
	 * Create a message embed to show the raid
	 * 
	 * @param raid
	 *            The raid object
	 * @return The embedded message
	 */
	private static MessageEmbed buildEmbed(PendingRaid raid) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(raid.getName());
		return builder.build();
	}

	public static List<Raid> getRaids() {
		return raids;
	}
}
