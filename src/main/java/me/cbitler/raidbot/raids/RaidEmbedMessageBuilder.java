package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.utility.EnvVariables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

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

    /**
     * Build the not responded text
     *
     * @return The not responded text
     */
    private static String buildNotRespondedText(List<Member> notRespondedMembers) {
        StringBuilder response = new StringBuilder();

        for (Member member : notRespondedMembers) {
            response.append("- ").append(member.getEffectiveName()).append("\n");
        }

        return response.toString();
    }

    /**
     * Build the role text, which shows the roles users are playing in the raids
     *
     * @return The role text
     */
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
                if(!role.equals("Can't Attend")){
                    roleStrings.add("- "+ guild.getMemberById(user.id).getEffectiveName() + " " + createSignupStatusText(user));
                }else{
                    roleStrings.add("- "+ guild.getMemberById(user.id).getEffectiveName());
                }

            }
        }
        return roleTexts;
    }

    private static String createSignupStatusText(RaidUser user){
        Emote emote;
        if(user.isAccepted()){
            if(EnvVariables.getInstance().isTestEnvironment()){
                emote = RaidBot.getInstance().getJda().getEmoteById("590293224250408981");
            }else{
                emote = RaidBot.getInstance().getJda().getEmoteById("590293759166775307");
            }
        }else if(user.isBenched()){
            if(EnvVariables.getInstance().isTestEnvironment()) {
                emote = RaidBot.getInstance().getJda().getEmoteById("590292731436335166");
            }else{
                emote = RaidBot.getInstance().getJda().getEmoteById("590293760726925372");
            }
        }else if(user.isNoShow()){
            if(EnvVariables.getInstance().isTestEnvironment()) {
                emote = RaidBot.getInstance().getJda().getEmoteById("597751514307493888");
            }else{
                emote = RaidBot.getInstance().getJda().getEmoteById("605719936123404298");
            }
        }else{
            if(EnvVariables.getInstance().isTestEnvironment()) {
                emote = RaidBot.getInstance().getJda().getEmoteById("590292700842950656");
            }else{
                emote = RaidBot.getInstance().getJda().getEmoteById("590293759519096832");
            }
        }

        return "<:" + emote.getName() + ":" + emote.getId() + ">";
    }
}
