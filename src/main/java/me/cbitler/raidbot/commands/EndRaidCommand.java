package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class EndRaidCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Member member = channel.getGuild().getMember(author);
                
        if(PermissionsUtil.isRaidLeader(member)) {
            if(args.length >= 1) {
                String raidId = args[0];
                Raid raid = RaidManager.getRaid(raidId);
                if (raid != null && raid.serverId.equalsIgnoreCase(channel.getGuild().getId())) {
                    boolean deleted = RaidManager.deleteRaid(raidId);
                    if (deleted) {
                        author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Raid ended").queue());
                    } else {
                        author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("An error occured ending the raid").queue());
                    }
                } else {
                    author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("That raid doesn't exist on this server.").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - End a raid, removing the message and putting it in the archive. Only usable by raid leaders. ";
    }

    @Override
    public String commandName() {
        return "endRaid";
    }

    @Override
    public String commandParameters(){
        return "[raidId]";
    }
}
