package mintey.raidbot.raids;

import mintey.raidbot.RaidBot;
import mintey.raidbot.utility.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RaidEmbedMessageBuilder {

    public static MessageEmbed buildEmbed(Raid raid) {
        List<Member> notRespondedMembers = raid.getNotRespondedMembers();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("**" + raid.name + "**");
        builder.addBlankField(false);

        List<List<String>> roleTexts = buildRolesText(raid);

        int uglyLoopCounterIndex = 0;
        for(List<String> role: roleTexts){

            String header = role.get(0);
            role.remove(0);

            StringBuilder fullRoleText = new StringBuilder();
            for(String roleText : role){
                fullRoleText.append(roleText).append("\n");
            }
            builder.addField(header, fullRoleText.toString(), true);
            uglyLoopCounterIndex++;
            if(uglyLoopCounterIndex == 3){
                builder.addBlankField(false);
            }
        }

        builder.addField("**Not responded: ("+notRespondedMembers.size()+")**", buildNotRespondedText(notRespondedMembers) , true);
        builder.addBlankField(false);
        builder.addField("ID: ", raid.messageId, true);

        return builder.build();
    }
    private static String buildNotRespondedText(List<Member> notRespondedMembers) {
        StringBuilder response = new StringBuilder();

        for (Member member : notRespondedMembers) {
            response.append("- ").append(member.getEffectiveName()).append("\n");
        }

        return response.toString();
    }
    private static List<List<String>> buildRolesText(Raid raid) {
        RaidBot bot = RaidBot.getInstance();
        Guild guild = bot.getServer(raid.serverId);

        List<List<String>> roleTexts = new ArrayList<>();

        for (String role : raid.roles) {
            ArrayList<String> roleStrings = new ArrayList<>();
            roleTexts.add(roleStrings);

            List<RaidUser> usersInRole = raid.getUsersInRole(role);
            usersInRole.sort(Comparator.comparing((RaidUser u) -> u.signupTime));

            roleStrings.add("**" + role + " (" + usersInRole.size() + "):**");

            for (RaidUser user : usersInRole) {
                Member member = guild.retrieveMemberById(user.id).complete();
                if(!role.equals("Can't Attend")){
                    roleStrings.add("- "+ member.getEffectiveName() + " " + createSignupStatusText(user, raid));
                }else{
                    roleStrings.add("- "+ member.getEffectiveName());
                }

            }
        }
        return roleTexts;
    }

    private static String createSignupStatusText(RaidUser user, Raid raid){
        Emote emote;
        if(user.isAccepted()){
            emote = RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(raid.serverId, ServerSettings.AcceptedEmote));
        }else if(user.isBenched()){
            emote = RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(raid.serverId, ServerSettings.BenchedEmote));
        }else if(user.isNoShow()){
            emote = RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(raid.serverId, ServerSettings.NoShowEmote));
        }else{
            emote = RaidBot.getInstance().getJda().getEmoteById(ServerSettings.getInstance().loadServerSetting(raid.serverId, ServerSettings.NotDecidedEmote));
        }

        return "<:" + emote.getName() + ":" + emote.getId() + ">";
    }
}
