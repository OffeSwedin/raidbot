package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class RemindRaidCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        if(PermissionsUtil.isRaidLeader(member)) {
            if (args.length >= 1) {
                String raidId = args[0];
                Raid raid = RaidManager.getRaid(raidId);

                List<Member> notRespondedMembers = raid.getNotRespondedMembers();

                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Starting to remind " + notRespondedMembers.size() + " persons about raid: \"" + raid.name + "\" (ID: " + raid.messageId + ")").queue());
                for(Member notRespondedMember : notRespondedMembers){
                    notRespondedMember.getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Reminder to please sign up for the raid: \"" + raid.name + "\" (ID: " + raid.messageId + ")").queue());
                }

                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Reminded everyone successfully. ").queue());
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Reminds all non-responded members to sign for the raid. Only usable by raid leaders. ";
    }

    @Override
    public String commandName() {
        return "remind";
    }

    @Override
    public String commandParameters(){
        return "[raidId]";
    }
}
