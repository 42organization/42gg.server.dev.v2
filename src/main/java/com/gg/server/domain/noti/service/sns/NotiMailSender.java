package com.gg.server.domain.noti.service.sns;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.AsyncMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotiMailSender {
    private final JavaMailSender javaMailSender;
    private final AsyncMailSender asyncMailSender;
    private final NotiService notiService;

    public void send(UserNotiDto user, Noti noti) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setSubject("핑퐁요정🧚으로부터 도착한 편지");
            log.info(user.getEmail());
            helper.setTo(user.getEmail());
            helper.setText(notiService.getMessage(noti));
        } catch (MessagingException e) {
            log.error("MessagingException message = {}", e.getMessage());
        }
        log.info("Email send {}", user.getUserId());
        asyncMailSender.send(message);
    }

    public void send(UserDto user, Noti noti) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setSubject("핑퐁요정🧚으로부터 도착한 편지");
            log.info(user.getEMail());
            helper.setTo(user.getEMail());
            helper.setText(notiService.getMessage(noti));
        } catch (MessagingException e) {
            log.error("MessagingException message = {}", e.getMessage());
        }
        log.info("Email send {}", user.getId());
        asyncMailSender.send(message);
    }
}
