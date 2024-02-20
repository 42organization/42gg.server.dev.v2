package gg.pingpong.api.user.noti.service.sns;

public class SlackbotUtils {
	public static String conversationsUrl = "https://slack.com/api/conversations.open";
	public static String sendMessageUrl = "https://slack.com/api/chat.postMessage";
	public static String userIdGetUrl = "https://slack.com/api/users.lookupByEmail";
	public static String authenticationPrefix = "Bearer ";
	public static String intraEmailSuffix = "@student.42seoul.kr";
}
