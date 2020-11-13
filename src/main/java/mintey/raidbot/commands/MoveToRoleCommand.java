package mintey.raidbot.commands;

import mintey.raidbot.raids.Raid;
import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.raids.RaidUser;
import mintey.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Timestamp;

public class MoveToRoleCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.retrieveMember(author).complete();

        if (member != null && PermissionsUtil.isRaidLeader(member)) {
            if (args.length >= 2) {
                String messageId = args[0];
                String userName = args[1];
                String roleName = combineArguments(args, 2);

                Raid raid = RaidManager.getRaid(messageId);

                if (raid != null && raid.serverId.equalsIgnoreCase(channel.getGuild().getId())) {
                    if(raid.roleExists(roleName)){
                        Member serverMember = guild.getMembersByEffectiveName(userName, true).stream().findFirst().orElse(null);

                        if(serverMember != null){
                            String userId = serverMember.getUser().getId();
                            RaidUser user = raid.getRaidUser(userId);

                            if (user != null) {
                                user.role = roleName;
                                user.save();
                            } else {
                                user = new RaidUser(userId, userName, "", roleName, new Timestamp(System.currentTimeMillis()), raid.messageId);
                                raid.addUser(user, true);
                            }

                            raid.updateMessage();
                        } else {
                            author.openPrivateChannel()
                                    .queue(privateChannel -> privateChannel.sendMessage("User does not exist in the server").queue());
                        }
                    } else {
                        author.openPrivateChannel()
                                .queue(privateChannel -> privateChannel.sendMessage("Role does not exist in the raid").queue());
                    }
                } else {
                    author.openPrivateChannel()
                            .queue(privateChannel -> privateChannel.sendMessage("Non-existant raid").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Move a player to the specified role in a raid. Only usable by raid leaders. ";
    }

    @Override
    public String commandName() {
        return "moveToRole";
    }

    @Override
    public String commandParameters(){
        return "[raidId] [userName] [roleName]";
    }
}
