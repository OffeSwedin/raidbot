package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import me.cbitler.raidbot.utility.ServerSettings;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger log = LoggerFactory.getLogger(RaidManager.class);

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

				Raid newRaid = new Raid(message.getId(), message.getGuild().getId(),
						message.getChannel().getId(), raidText, roles);
				boolean inserted = newRaid.save();

				if (inserted) {
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
			log.error("Error encountered in sending message.", e);
			throw e;
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
		Raid raid = getRaid(messageId);

		if (raid != null) {
			try {

				Guild guild = RaidBot.getInstance().getServer(raid.serverId);
				TextChannel archiveChannel = guild
						.getTextChannelsByName(ServerSettings.getInstance().getArchiveChannel(raid.serverId), true).get(0);

				archiveChannel.sendMessage(RaidEmbedMessageBuilder.buildEmbed(raid)).queue();

				guild.getTextChannelById(raid.channelId).getMessageById(messageId)
                        .queue(message -> message.delete().queue());

			} catch (Exception e) {
				log.error("Tried to delete raid without message", e);
			}

			if(raid.delete()){
                raids.removeIf((Raid r) -> r.messageId.equalsIgnoreCase(messageId));

                return true;
            }
		}

		return false;
	}

	/**
	 * Load raids This first queries all of the raids and loads the raid data
	 * and adds the raids to the raid list Then, it queries the raid users and
	 * inserts them into their relevant raids, updating the embedded messages
	 */
	public static void loadRaidsFromDatabase() {
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
					ArrayList<String> roles = new ArrayList<>();
					String[] roleSplit = rolesText.split(";");
					Collections.addAll(roles, roleSplit);

					Raid raid = new Raid(messageId, serverId, channelId, name, roles);
					raids.add(raid);
				} catch (Exception e) {
					log.error("Raid couldn't load: " + e.getMessage());
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
					RaidUser user = new RaidUser(id, name, spec, role, signupStatus, timestamp, raid.messageId);
					raid.addUser(user, false);
				}
			}

			int delay = 0;
			for (Raid raid : raids) {
				try{
					raid.parseReactions();
					raid.updateMessage();
					delay = raid.resetReactions(delay);
				} catch (ErrorResponseException e){
					if(e.getErrorCode() == 10008){ //MessageNotFound
						raid.delete();
					}
				}
			}
		} catch (SQLException e) {
			log.error("Couldn't load raids.. exiting", e);
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
        return raids.stream().filter(
                (Raid raid) -> raid.messageId.equalsIgnoreCase(messageId)
        ).findFirst().orElse(null);
	}

	public static List<Raid> getRaids() {
		return raids;
	}
}
