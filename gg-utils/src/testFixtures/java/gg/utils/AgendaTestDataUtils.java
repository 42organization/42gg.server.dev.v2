package gg.utils;

import gg.data.agenda.Agenda;
import gg.utils.fixture.agenda.AgendaAnnouncementFixture;
import gg.utils.fixture.agenda.AgendaFixture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgendaTestDataUtils {

	private final AgendaFixture agendaFixture;

	private final AgendaAnnouncementFixture agendaAnnouncementFixture;

	public Agenda createAgendaAndAnnouncements(int size) {
		Agenda agenda = agendaFixture.createAgenda();
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size / 2, true);
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size - size / 2, false);
		return agenda;
	}
}
