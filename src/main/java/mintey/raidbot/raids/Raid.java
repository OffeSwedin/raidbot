package mintey.raidbot.raids;

import mintey.raidbot.RaidBot;
import mintey.raidbot.database.Database;
import mintey.raidbot.database.QueryResult;
import mintey.raidbot.utility.PermissionsUtil;
import mintey.raidbot.utility.Reaction;
import mintey.raidbot.utility.Reactions;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Raid {
	private static final Logger log = LoggerFactory.getLogger(Raid.class);

	public final String messageId;
	public final String serverId;
	public final String channelId;

	public String name;

	protected final List<String> roles;
	private final List<RaidUser> users = new ArrayList<>();
	public Raid(String messageId, String serverId, String channelId, String name, List<String> roles) {
		this.messageId = messageId;
		this.serverId = serverId;
		this.channelId = channelId;
		this.name = name;
		this.roles = roles;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addUser(RaidUser user, boolean db_insert) {
		if (db_insert) {
			user.save();
		}

		users.add(user);
	}
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
	public RaidUser getRaidUser(String userId){
        for (RaidUser user : users){
            if(user.id.equalsIgnoreCase(userId)){
                return user;
            }
        }

        return null;
    }
	public List<RaidUser> getUsersInRole(String role) {
		List<RaidUser> usersInRole = new ArrayList<>();

		for (RaidUser user : users){
			if (user.role.equalsIgnoreCase(role)){
				usersInRole.add(user);
			}
		}

		return usersInRole;
	}

	public List<Member> getNotRespondedMembers() {
		RaidBot bot = RaidBot.getInstance();
		Guild guild = bot.getServer(serverId);

		List<Member> members = guild.getMembers();
		List<Member> notRespondedMembers = new ArrayList<>();

		for (Member member : members) {
			if (getRaidUser(member.getUser().getId()) == null) {
				if (PermissionsUtil.isRaider(member)) {
					notRespondedMembers.add(member);
				}
			}
		}
		return notRespondedMembers;
	}

	public List<String> getNotDecidedNames(){
		return this.users.stream()
				.filter(user -> !user.isAccepted() &&  !user.isBenched() && !user.isNoShow())
				.map(raidUser -> raidUser.name)
				.collect(Collectors.toList());
	}

	public boolean roleExists(String role){
		return roles.contains(role);
	}
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
		Guild guild = RaidBot.getInstance().getServer(serverId);
		Message message = RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId).retrieveMessageById(messageId).complete();
		for(MessageReaction reaction : message.getReactions()){
			List<User> users = reaction.retrieveUsers().complete();
			for(User user : users){
				if(!user.isBot()){
					parseReaction(guild.retrieveMember(user).complete(), reaction.getReactionEmote().getEmote(), false);
				}
			}
		}
	}

	public void parseReaction(Member member, Emote emote, boolean update_message){
		if (PermissionsUtil.isAllowedToRaid(member)) {

			String emojiId = emote.getId();
			Reaction reaction = Reactions.getSignupReactionFromEmojiId(emojiId, serverId);
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
			for (Reaction reaction : Reactions.getSignupReactions(serverId)){
				RaidBot.getInstance().getServer(serverId).getTextChannelById(channelId).addReactionById(messageId, reaction.getEmote()).queueAfter(delay, TimeUnit.MILLISECONDS);
				delay += 1000;
			}
		} catch (Exception e) {
			log.error("Could not reset reactions. ", e);
		}

		return delay;
	}
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
