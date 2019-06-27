package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class AcceptToRaidCommand extends Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        if (PermissionsUtil.hasRaidLeaderRole(member)) {
            if (args.length >= 2) {
                String messageId = args[0];
                String name = args[1];

                Raid raid = RaidManager.getRaid(messageId);

                if (raid != null && raid.getServerId().equalsIgnoreCase(channel.getGuild().getId())) {
                    boolean accepted = raid.acceptUser(name);
                    if(accepted){
                        raid.updateMessage();
                    }else{
                        author.openPrivateChannel()
                                .queue(privateChannel -> privateChannel.sendMessage("Player '" + name + "' does not exist in raid. ").queue());
                    }
                } else {
                    author.openPrivateChannel()
                            .queue(privateChannel -> privateChannel.sendMessage("Non-existant raid").queue());
                }
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for !accept: !accept [raid id] [name]").queue());
            }
        }
    }
}
