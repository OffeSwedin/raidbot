package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class EditRaidCommand extends Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);
        RaidBot bot = RaidBot.getInstance();

        if(PermissionsUtil.hasRaidLeaderRole(member)) {
            if (args.length >= 2) {
                String raidId = args[0];
                String raidText = combineArguments(args, 1);
                RaidManager.editRaid(raidId, raidText);
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for !editRaid: !editRaid [raidID] [raidText]").queue());
            }
        }
    }
}
