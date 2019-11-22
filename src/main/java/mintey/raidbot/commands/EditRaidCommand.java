package mintey.raidbot.commands;

import mintey.raidbot.raids.Raid;
import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class EditRaidCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(author);

        if(PermissionsUtil.isRaidLeader(member)) {
            if (args.length >= 2) {
                String raidId = args[0];
                String raidText = combineArguments(args, 1);
                Raid raid = RaidManager.getRaid(raidId);

                raid.setName(raidText);
                raid.save();
                raid.updateMessage();
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Edits a raids raid-text to the specified one. Only usable by raid leaders. ";
    }

    @Override
    public String commandName() {
        return "editRaid";
    }

    @Override
    public String commandParameters(){
        return "[raidId] [raidText]";
    }
}
