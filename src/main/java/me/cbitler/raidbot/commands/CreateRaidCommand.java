package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation.RunNameStep;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CreateRaidCommand implements Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);
        RaidBot bot = RaidBot.getInstance();

        if(PermissionsUtil.hasRaidLeaderRole(member)) {
            try {
                CreationStep runNameStep = new RunNameStep(guild.getId());
                author.openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(runNameStep.getStepText()).queue());
                bot.getCreationMap().put(author.getId(), runNameStep);
            } catch (Exception exc) {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Make sure that the bot has the 'Manage messages' permission").queue());
            }
        }
    }
}
