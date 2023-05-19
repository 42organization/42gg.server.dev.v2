package com.gg.server.global.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@AllArgsConstructor
@Slf4j
public class AsyncMailSender {
    private final JavaMailSender javaMailSender;

    @Async("asyncExecutor")
    public void send(MimeMessage message) {
        try {
                javaMailSender.send(message);
        } catch(Exception ex) {
            log.error(ex.getMessage());
        }
        log.info("java mail send complete");
    }
}
