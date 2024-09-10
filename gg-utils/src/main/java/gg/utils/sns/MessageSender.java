package gg.utils.sns;

import java.util.List;

public interface MessageSender {

	void send(String intraUsername, String message);

	void sendGroup(List<String> intraUsernames, String message);
}
