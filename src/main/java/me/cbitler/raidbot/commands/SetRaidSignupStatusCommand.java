package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
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

        if (PermissionsUtil.hasRaidLeaderRole(member)) {
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
                        .sendMessage(commandFormat() + "").queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Accepts players to a raid. Only usable by raid leaders";
    }

    @Override
    public String commandParameters(){
        return "[raidId] [userName] [userName] [...]";
    }

    public abstract boolean handleUser(Raid raid, String userName);
}
