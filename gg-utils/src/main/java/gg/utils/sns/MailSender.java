package gg.utils.sns;

public interface MailSender {

	void send(String emailTo, String subject, String text);
}
