package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation.RunNameStep;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidUser;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class RemoveFromRaidCommand implements Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        if (args.length > 2) {
            String messageId = args[1];
            String name = args[2];

            Raid raid = RaidManager.getRaid(messageId);

            if (raid != null && raid.getServerId().equalsIgnoreCase(channel.getGuild().getId())) {
                RaidUser user = raid.getUserByName(name);
                if (user != null) {
                    raid.removeUser(user.id);
                }
            } else {
                author.openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage("Non-existant raid").queue());
            }
        } else {
            author.openPrivateChannel().queue(privateChannel -> privateChannel
                    .sendMessage("Format for !removeFromRaid: !removeFromRaid [raid id] [name]").queue());
        }
    }
}
