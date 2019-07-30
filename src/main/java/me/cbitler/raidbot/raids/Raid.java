package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.Reaction;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * Represents a raid and has methods for adding/removing users,
 * etc
 */
public class Raid {
	private static final Logger log = LoggerFactory.getLogger(Raid.class);

	public final String messageId;
	public final String serverId;
	public final String channelId;

	public String name;

	protected final List<String> roles;
	private final List<RaidUser> users = new ArrayList<>();

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
	 */
	public Raid(String messageId, String serverId, String channelId, String name, List<String> roles) {
		this.messageId = messageId;
		this.serverId = serverId;
		this.channelId = channelId;
		this.name = name;
		this.roles = roles;
	}

	/**
	 * Get the channel ID for this raid
	 *
	 * @param name
	 *		The new raidText for this raid
	 */
	public void setName(String name) {
		this.name = name;
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
	public void addUser(RaidUser user, boolean db_insert) {
		if (db_insert) {
			user.save();
		}

		users.add(user);
	}

    /**
     * Remove a user from this raid. This also updates the database to remove
     * them from the raid
     *
     * @param id
     *            The user's id
     */
    public void removeUser(String id) {
        boolean removed = users.removeIf((RaidUser user) -> user.id.equalsIgnoreCase(id));

        if(removed){
            try {
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
                        new String[] { id, messageId });
            } catch (SQLException e) {
                log.error("Could not remove user from raid. ", e);
            }
        }
    }

    /**
     * Accept a user to a raid
     *
     * @param username The username of the User to be accepted
     * @return If the user was accepted or not
     */
	public boolean acceptUser(String username){
		for(RaidUser user : users){
			if(user.name.equalsIgnoreCase(username)){
				user.accept();
				user.save();
				return true;
			}
		}
		return false;
	}

    /**
     * Bench a user for a raid
     *
     * @param username The username of the User to be benched
     * @return If the user was accepted or not
     */
	public boolean standbyUser(String username){
		for(RaidUser user : users){
			if(user.name.equalsIgnoreCase(username)){
				user.bench();
				user.save();
				return true;
			}
		}
		return false;
	}

	/**
	 * Bench a user for a raid
	 *
	 * @param username The username of the User to be benched
	 * @return If the user was accepted or not
	 */
	public boolean noShowUser(String username){
		for(RaidUser user : users){
			if(user.name.equalsIgnoreCase(username)){
				user.noShow();
				user.save();
				return true;
			}
		}
		return false;
	}


    /**
     * Gets the specified RaidUser from the raid
     * @param userId The user to get
     * @return The user, or null if not found
     */
	public RaidUser getRaidUser(String userId){
        for (RaidUser user : users){
            if(user.id.equalsIgnoreCase(userId)){
                return user;
            }
        }

        return null;
    }

	/**
	 * Get list of users in a role
	 *
	 * @param role
	 *            The name of the role
	 * @return The users in the role
	 */
	public List<RaidUser> getUsersInRole(String role) {
		List<RaidUser> usersInRole = new ArrayList<>();

		for (RaidUser user : users){
			if (user.role.equalsIgnoreCase(role)){
				usersInRole.add(user);
			}
		}

		return usersInRole;
	}

	/**
	 * Checks if the role exists in the raid
	 *
	 * @param role The role to check if it exists
	 * @return If the role exists or not
	 */
	public boolean roleExists(String role){
		return roles.contains(role);
	}
		
	/**
	 * Update the embedded message for the raid
	 */
	public void updateMessage() {
		MessageEmbed embed = RaidEmbedMessageBuilder.buildEmbed(this);

		try {
			MessageBuilder mb = new MessageBuilder();
			mb.setEmbed(embed);
			mb.setContent(" ");
			Message message = mb.build();
			RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId)
					.editMessageById(messageId, message).queue();
		} catch (Exception e) {
			log.error("Could not update message. ", e);
		}
	}

	public void parseReactions() {
		Guild guild = RaidBot.getInstance().getJda().getGuilds().get(0);
		Message message = RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId).getMessageById(messageId).complete();
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
		if (PermissionsUtil.isAllowedToRaid(member)) {

			String emojiId = emote.getId();
			Reaction reaction = Reactions.getReactionFromEmojiId(emojiId);
			if (reaction != null) {
				if (getRaidUser(member.getUser().getId()) != null) {
					removeUser(member.getUser().getId());
				}

				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				RaidUser user = new RaidUser(member.getUser().getId(), member.getEffectiveName(), "", reaction.getSpec(), timestamp, messageId);
				addUser(user, true);

				if(update_message){
					updateMessage();
				}
			}
		}
	}

	public int resetReactions(int delay){
		try {
			RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId).clearReactionsById(messageId).queueAfter(delay, TimeUnit.MILLISECONDS);
			delay += 1000;
			for (Reaction reaction : Reactions.getReactions()){
				RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId).addReactionById(messageId, reaction.getEmote()).queueAfter(delay, TimeUnit.MILLISECONDS);
				delay += 1000;
			}
		} catch (Exception e) {
			log.error("Could not reset reactions. ", e);
		}

		return delay;
	}

	/**
	 * Save the raid in the database
	 */
	public boolean save() {
		RaidBot bot = RaidBot.getInstance();
		Database db = bot.getDatabase();

		String roleString = String.join(";", roles);

		try {
			QueryResult results = db.query("SELECT COUNT(*) FROM `raids` WHERE `raidId` = ? AND `serverId` = ?", new String[] { messageId, serverId });
			results.getResults().next();
			int count = Integer.parseInt(results.getResults().getString("COUNT(*)"));

			results.getResults().close();
			results.getStmt().close();

			if (count == 0) {
				db.update( "INSERT INTO `raids` " +
								"(`raidId`, `serverId`, `channelId`, `leader`, `name`, `description`, `date`, `time`, `roles`) " +
								"VALUES (?,?,?,?,?,?,?,?,?)",
						new String[] { messageId, serverId, channelId, "", name, "", "", "", roleString });
			} else {
				db.update( "UPDATE `raids` SET `name` = ? WHERE `raidId` = ? AND `serverId` = ?",
						new String[] { name, messageId, serverId });
			}
		} catch (SQLException e) {
			log.error("Could not update raid. ", e);
			return false;
		}

		return true;
	}

	public boolean delete(){
        try {
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raids` WHERE `raidId` = ?",
                    new String[] { messageId });
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `raidId` = ?",
                    new String[] { messageId });
        } catch (Exception e) {
            log.error("Error encountered deleting raid");
            return false;
        }

        return true;
    }
}
