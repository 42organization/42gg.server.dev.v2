package gg.utils;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.utils.fixture.agenda.AgendaAnnouncementFixture;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgendaTestDataUtils {

	private final AgendaFixture agendaFixture;

	private final AgendaAnnouncementFixture agendaAnnouncementFixture;

	private final AgendaTeamFixture agendaTeamFixture;

	public Agenda createAgendaAndAnnouncements(int size) {
		Agenda agenda = agendaFixture.createAgenda();
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size / 2, true);
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size - size / 2, false);
		return agenda;
	}

	public Agenda createAgendaAndAgendaTeams(String intraId, int i, AgendaStatus status) {
		Agenda agenda = agendaFixture.createAgenda(intraId, status);
		agendaTeamFixture.createAgendaTeamList(agenda, AgendaTeamStatus.CONFIRM, i);
		return agenda;
	}
}
