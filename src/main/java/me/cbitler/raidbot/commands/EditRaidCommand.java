package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class EditRaidCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        if(PermissionsUtil.hasRaidLeaderRole(member)) {
            if (args.length >= 2) {
                String raidId = args[0];
                String raidText = combineArguments(args, 1);
                Raid raid = RaidManager.getRaid(raidId);

                raid.setName(raidText);
                raid.save();
                raid.updateMessage();
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for !editRaid: !editRaid [raidID] [raidText]").queue());
            }
        }
    }
}
