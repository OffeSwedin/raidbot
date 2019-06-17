package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidUser;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class BenchForRaidCommand implements Command {
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
                    boolean benched = raid.benchUser(name);
                    if(benched){
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
                        .sendMessage("Format for !bench: !bench [raid id] [name]").queue());
            }
        }
    }
}
