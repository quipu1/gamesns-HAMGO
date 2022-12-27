package com.web.curation.config;

import io.swagger.models.auth.In;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Configuration
public class DiscordJdaConfig {
    private @Value("${jda.discord.guild.id}")
    Long guildId;

    private @Value("${jda.discord.guild.category.id}")
    Long categoryId;

    private JDA jda;

    public DiscordJdaConfig(@Value("${jda.discord.token}") String discordToken) {
        try {
            jda = JDABuilder.createDefault(discordToken)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkMember(String userTag) {
        Optional<User> getMember = Optional.ofNullable(jda.getUserByTag(userTag));

        if (getMember.isPresent()) {
            return true;
        }

        return false;
    }

    public String createVoiceChannel(String channelName, List<String> userTagList) throws ExecutionException, InterruptedException {
        String channelUrl = "";
        Guild guild = jda.getGuildById(guildId);

        Category category = guild.getCategoryById(categoryId);

//        VoiceChannel voiceChannel = category.createVoiceChannel(channelName).submit().get().;

        try {
            VoiceChannel voiceChannel = category.createVoiceChannel(channelName)
                    .addPermissionOverride(guild.getPublicRole(), EnumSet.of(Permission.VOICE_CONNECT), EnumSet.of(Permission.VIEW_CHANNEL))
                    .reason("매칭 완료 방 생성").submit().get();

            for (String userTag : userTagList) {
                Member member = guild.getMemberByTag(userTag);
                voiceChannel.createPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();
            }

            voiceChannel.getManager().setUserLimit(5).queue();
            channelUrl = voiceChannel.createInvite().setMaxAge(300).submit().get().getUrl();
//                .queue((r)  -> {
//                    for (String userTag : userTagList) {
//                        Member member = guild.getMemberByTag(userTag);
//                        r.createPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();
//                    }
//
//                    r.getManager().setUserLimit(5).queue();
//
//                    r.createInvite().setMaxAge(300).queue((rr) -> {
//                        return rr.getUrl();
//                    });
//                });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return channelUrl;
    }

    public void deleteVoiceChannel() {
        Guild guild = jda.getGuildById(guildId);
        System.out.println("현재 길드(서버) : " + guild.toString());

        List<VoiceChannel> channelList = guild.getVoiceChannels();

        System.out.println("채널 목록   ------> ");
        for (VoiceChannel guildChannel : channelList) {
            System.out.print(guildChannel);

            List<Member> memberList = guildChannel.getMembers();
            if (memberList.size() == 0 || memberList.isEmpty()) {
                System.out.print(" ---> 삭제완료");
                guildChannel.delete().reason("사용자가 없으므로 제거").queue();
            }

            System.out.println();
        }
        System.out.println("채널 목록 끝 ------ ");
    }
}
