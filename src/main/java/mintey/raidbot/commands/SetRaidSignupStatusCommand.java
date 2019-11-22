package mintey.raidbot.commands;

import mintey.raidbot.raids.Raid;
import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SetRaidSignupStatusCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        if (PermissionsUtil.isRaidLeader(member)) {
            if (args.length >= 2) {
                List<String> names = new ArrayList<>(Arrays.asList(args));
                String messageId = names.remove(0);

                Raid raid = RaidManager.getRaid(messageId);

                if (raid != null && raid.serverId.equalsIgnoreCase(channel.getGuild().getId())) {
                    boolean needToUpdateMessage = false;
                    for (String name : names){
                        if(handleUser(raid, name)) {
                            needToUpdateMessage = true;
                        }else{
                            author.openPrivateChannel()
                                    .queue(privateChannel -> privateChannel.sendMessage("Player '" + name + "' does not exist in raid. ").queue());
                        }
                    }
                    if(needToUpdateMessage){
                        raid.updateMessage();
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
    public String commandParameters(){
        return "[raidId] [userName] [userName] [...]";
    }

    public abstract String helpMessage();

    public abstract boolean handleUser(Raid raid, String userName);
}
