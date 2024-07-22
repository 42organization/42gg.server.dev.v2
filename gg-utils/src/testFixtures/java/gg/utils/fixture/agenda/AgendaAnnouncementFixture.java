package gg.utils.fixture.agenda;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.repo.agenda.AgendaAnnouncementRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgendaAnnouncementFixture {

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

	private final EntityManager em;

	public AgendaAnnouncement createAgendaAnnouncement(Agenda agenda) {
		AgendaAnnouncement announcement = AgendaAnnouncement.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.isShow(true)
			.agenda(agenda)
			.build();
		em.persist(announcement);
		em.flush();
		em.clear();
		return announcement;
	}

	public List<AgendaAnnouncement> createAgendaAnnouncementList(Agenda agenda, int size) {
		List<AgendaAnnouncement> announcements = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			announcements.add(AgendaAnnouncement.builder()
				.title("title " + i)
				.content("content " + i)
				.isShow(true)
				.agenda(agenda)
				.build());
		}
		return agendaAnnouncementRepository.saveAll(announcements);
	}

	public List<AgendaAnnouncement> createAgendaAnnouncementList(Agenda agenda, int size, boolean isShow) {
		List<AgendaAnnouncement> announcements = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			announcements.add(AgendaAnnouncement.builder()
				.title("title " + i)
				.content("content " + i)
				.isShow(isShow)
				.agenda(agenda)
				.build());
		}
		return agendaAnnouncementRepository.saveAll(announcements);
	}
}
