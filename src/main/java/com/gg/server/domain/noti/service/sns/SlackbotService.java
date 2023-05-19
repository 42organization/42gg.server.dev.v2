package com.gg.server.domain.noti.service.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.exception.SlackJsonParseException;
import com.gg.server.domain.noti.exception.SlackSendException;
import com.gg.server.domain.noti.exception.SlackUserGetFailedException;
import com.gg.server.domain.noti.service.NotiService;
import lombok.Getter;
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
public class SlackbotService {
    @Value("${slack.xoxbToken}")
    private String authenticationToken;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final NotiService notiService;
    public SlackbotService(RestTemplateBuilder builder, ObjectMapper objectMapper, NotiService notiService) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
        this.notiService = notiService;
    }

    private String getSlackUserId(String intraId) throws SlackUserGetFailedException {
        String userEmail = intraId + intraEmailSuffix;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.AUTHORIZATION, authenticationPrefix + authenticationToken);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("email", userEmail);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<SlackUserInfoResponse> responseEntity = restTemplate
                .exchange(userIdGetUrl, HttpMethod.POST, request, SlackUserInfoResponse.class);
        if (!responseEntity.getBody().ok)
            throw new SlackUserGetFailedException();
        return responseEntity.getBody().user.id;
    }

    private String getDmChannelId(String slackUserId) throws SlackJsonParseException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                authenticationPrefix + authenticationToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("users", slackUserId);
        String contentBody = null;
        try {
            contentBody = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new SlackJsonParseException();
        }

        HttpEntity<String> entity = new HttpEntity<>(contentBody, httpHeaders);

        ResponseEntity<ConversationResponse> responseEntity = restTemplate
                .exchange(conversationsUrl, HttpMethod.POST, entity, ConversationResponse.class);
        if(!responseEntity.getBody().ok) {
            log.error("fail to get user dm channel id");
            throw new SlackUserGetFailedException();
        }
        return responseEntity.getBody().channel.id;
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

    private void startSendNoti(String intraId, Noti noti) throws SlackSendException {
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
        String contentBody = null;
        try {
            contentBody = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("start send Slack Noti", e);
            throw new SlackJsonParseException();
        }

        HttpEntity<String> entity = new HttpEntity<>(contentBody, httpHeaders);

        ResponseEntity<String> respEntity = restTemplate
                .exchange(sendMessageUrl, HttpMethod.POST, entity, String.class);
        if(respEntity.getStatusCode() != HttpStatus.OK)
            throw new SlackSendException();
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
