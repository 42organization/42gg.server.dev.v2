package com.gg.server.domain.noti.service.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.exception.SlackJsonParseException;
import com.gg.server.domain.noti.exception.SlackSendException;
import com.gg.server.domain.noti.exception.SlackUserGetFailedException;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.gg.server.domain.noti.service.sns.SlackbotUtils.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlackbotService {
    @Value("${slack.xoxbToken}")
    private String authenticationToken;

    private final NotiService notiService;
    private final ApiUtil apiUtil;

    private String getSlackUserId(String intraId) {
        String userEmail = intraId + intraEmailSuffix;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.AUTHORIZATION, authenticationPrefix + authenticationToken);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("email", userEmail);

        SlackUserInfoResponse res = apiUtil.apiCall(userIdGetUrl, SlackUserInfoResponse.class, headers, parameters, HttpMethod.POST);
        return res.user.id;
    }

    private String getDmChannelId(String slackUserId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                authenticationPrefix + authenticationToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("users", slackUserId);

        ConversationResponse res = apiUtil.apiCall(conversationsUrl, ConversationResponse.class,
                httpHeaders, bodyMap, HttpMethod.POST);

        return res.channel.id;
    }

    @Async("asyncExecutor")
    public void send(UserNotiDto user, Noti noti) {
        log.info("slack alarm send");
        try {
            startSendNoti(user.getIntraId(), noti);
        } catch (SlackSendException e) {
            log.error("SlackSendException message = {}", e.getMessage());
        }
    }

    @Async("asyncExecutor")
    public void send(UserDto user, Noti noti) {
        log.info("slack alarm send");
        try {
            startSendNoti(user.getIntraId(), noti);
        } catch (SlackSendException e) {
            log.error("SlackSendException message = {}", e.getMessage());
        }
    }

    private void startSendNoti(String intraId, Noti noti) {
        String slackUserId = getSlackUserId(intraId);
        String slackChannelId = getDmChannelId(slackUserId);
        String message = notiService.getMessage(noti);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                authenticationPrefix + authenticationToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("channel",slackChannelId);
        map.put("text", message);
        apiUtil.apiCall(sendMessageUrl, String.class, httpHeaders, map, HttpMethod.POST);
    }

    @Getter
    static class ConversationResponse {
        private Boolean ok;
        private Channel channel;

        @Getter
        static class Channel {
            private String id;
        }

    }

    @Getter
    static class SlackUserInfoResponse {
        private Boolean ok;
        private SlackUser user;

        @Getter
        static class SlackUser{
            private String id;
        }
    }
}
