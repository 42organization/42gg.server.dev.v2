package gg.utils.sns.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import gg.utils.sns.MailSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AsyncMailSender implements MailSender {

	private final JavaMailSender javaMailSender;

	@Async("asyncExecutor")
	public void send(String emailTo, String subject, String text) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(emailTo);
			helper.setSubject(subject);
			helper.setText(text);
			log.info("Send email to {}", emailTo);
			javaMailSender.send(message);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
}
