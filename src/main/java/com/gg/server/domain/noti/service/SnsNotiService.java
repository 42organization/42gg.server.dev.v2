package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.service.sns.MailSender;
import com.gg.server.domain.noti.service.sns.SlackbotService;
import com.gg.server.domain.noti.service.sns.SnsSender;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.SnsType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsNotiService {
    private final SnsSender notiMailSender;
    private final SnsSender slackbotService;

    public SnsNotiService(MailSender notiMailSender, SlackbotService slackbotService) {
        this.notiMailSender = notiMailSender;
        this.slackbotService = slackbotService;
    }

    public void sendSnsNotification(Noti noti, UserDto user) {
        SnsType userSnsNotiOpt = user.getSnsNotiOpt();
        if (userSnsNotiOpt == SnsType.NONE)
            return;
        if(userSnsNotiOpt == SnsType.EMAIL)
            notiMailSender.send(user, noti);
        else if (userSnsNotiOpt == SnsType.SLACK)
            slackbotService.send(user, noti);
        else if (userSnsNotiOpt == SnsType.BOTH) {
            notiMailSender.send(user, noti);
            slackbotService.send(user, noti);
        }
    }
}
