package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.List;

public class EndRaidCommand extends Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        Member member = channel.getGuild().getMember(author);
                
        if(PermissionsUtil.hasRaidLeaderRole(member)) {
            if(args.length >= 1) {
                String raidId = args[0];
                Raid raid = RaidManager.getRaid(raidId);
                if (raid != null && raid.getServerId().equalsIgnoreCase(channel.getGuild().getId())) {
                    //Get list of log messages and send them
                    if (args.length > 1) {
                        List<String> links = Arrays.asList(args);
                        raid.messagePlayersWithLogLinks(links);
                    }
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
                        .sendMessage("Format for !endRaid: !endRaid [raidId]").queue());
            }
        }
    }
}
