package mintey.raidbot.commands;

import mintey.raidbot.raids.RaidManager;
import mintey.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CreateRaidCommand extends Command {
    @Override
    public void handleCommand(String[] args, TextChannel channel, User author) {
        Guild guild = channel.getGuild();
        Member member = guild.retrieveMember(author).complete();

        if(member != null && PermissionsUtil.isRaidLeader(member)) {
            if (args.length >= 1) {
                String raidText = combineArguments(args, 0);
                RaidManager.createRaid(raidText, guild.getId());
            } else {
                author.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Format for command: " + commandFormat()).queue());
            }
        }
    }

    @Override
    public String helpMessage() {
        return commandFormat() + " - Creates a raid with the specified raid-text. Only usable by raid leaders. ";
    }

    @Override
    public String commandName() {
        return "createRaid";
    }

    @Override
    public String commandParameters(){
        return "[raidText]";
    }
}
