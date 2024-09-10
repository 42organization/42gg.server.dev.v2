package gg.agenda.api.utils;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;

@Component
public class SnsMessageUtil {
	private static final String URL = "https://gg.42seoul.kr/agenda/";
	private static final String SUBJECT = "행사요정🧚으로부터 도착한 편지";

	public String addAgendaAnnouncementMessage(Agenda agenda, AgendaAnnouncement newAnnounce) {
		String link = URL + "agenda_key=" + agenda.getAgendaKey() + "/announcement/" + newAnnounce.getId();
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의 새로운 공지사항이 도착했습니다."
			+ "\n" + newAnnounce.getTitle()
			+ "\n" + "$$" + link + "$$";
	}

	public String confirmAgendaMessage(Agenda agenda) {
		String link = URL + "agenda_key=" + agenda.getAgendaKey();
		return SUBJECT
			+ "\n" + agenda.getTitle() + "이 확정되었습니다."
			+ "\n" + "행사가 확정되었습니다. 시작일자와 장소를 확인해주세요!"
			+ "\n" + "$$" + link + "$$";
	}

	public String cancelAgendaMessage(Agenda agenda) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "이 취소되었습니다."
			+ "\n" + "아쉽게도 행사가 취소되었습니다. 다음에 다시 만나요!";
	}

	public String finishAgendaMessage(Agenda agenda) {
		String link = URL + "agenda_key=" + agenda.getAgendaKey();
		if (agenda.getIsRanking()) {
			return SUBJECT
				+ "\n" + agenda.getTitle() + "이 종료되었습니다."
				+ "\n" + "행사가 성공적으로 종료되었습니다. 수고하셨습니다!"
				+ "\n" + "결과 확인 $$" + link + "$$";
		} else {
			return SUBJECT
				+ "\n" + agenda.getTitle() + "이 종료되었습니다."
				+ "\n" + "행사가 성공적으로 종료되었습니다. 수고하셨습니다!";
		}
	}

	public String confirmTeamMessage(Agenda agenda, AgendaTeam agendaTeam) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의" + agendaTeam.getName() + "팀이 확정되었습니다."
			+ "\n" + "행사 확정을 기다려주세요!";
	}

	public String cancelTeamMessage(Agenda agenda, AgendaTeam agendaTeam) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의" + agendaTeam.getName() + "팀이 취소되었습니다.";
	}

	public String failTeamMessage(Agenda agenda) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의 팀이 취소되었습니다."
			+ "\n" + "행사가 확정되어 확정되지 않은 팀은 취소됩니다.";
	}

	public String attendTeamMateMessage(Agenda agenda, AgendaTeam agendaTeam, String intraId) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의" + agendaTeam.getName() + "팀에" + intraId + "님이 참가했습니다.";
	}

	public String leaveTeamMateMessage(Agenda agenda, AgendaTeam agendaTeam, String intraId) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "의" + agendaTeam.getName() + "팀에서" + intraId + "님이 탈퇴했습니다.";
	}

	public String agendaHostMinTeamSatisfiedMessage(Agenda agenda) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "행사가 최소 팀 개수를 충족했습니다."
			+ "\n" + "행사를 확정할 수 있습니다."
			+ "\n" + "확정시엔 다른 팀들이 참가 할 수 없으니, 주의하세요!"
			+ "\n" + "$$" + URL + "agenda_key=" + agenda.getAgendaKey() + "$$";
	}

	public String agendaHostMaxTeamSatisfiedMessage(Agenda agenda) {
		return SUBJECT
			+ "\n" + agenda.getTitle() + "행사가 최대 팀 개수를 충족했습니다."
			+ "\n" + "행사를 확정하고 진행 시간과 장소를 공지사항으로 전달해주세요."
			+ "\n" + "$$" + URL + "agenda_key=" + agenda.getAgendaKey() + "$$";
	}
}
