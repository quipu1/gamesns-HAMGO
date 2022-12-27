package com.web.curation.service;

import com.web.curation.config.DiscordJdaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class DiscordService {
    private final DiscordJdaConfig jdaConfig;

    @Transactional
    public Optional<String> createChannel(String channelName, List<String> userTagList) {
        Optional<String> url = Optional.ofNullable(null);

        try {
            url = Optional.ofNullable(jdaConfig.createVoiceChannel(channelName, userTagList));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return url;
    }

    // 매일 자정 12시마다 비어있는 방을 지운다.
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteChannel() {
        jdaConfig.deleteVoiceChannel();
    }

    @Transactional
    public boolean userCheck(String userTag) {
        return jdaConfig.checkMember(userTag);
    }
}
