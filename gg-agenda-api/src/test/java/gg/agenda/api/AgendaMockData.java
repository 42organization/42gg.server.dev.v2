package gg.agenda.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaMockData {

	private final EntityManager em;

	private final AgendaRepository agendaRepository;

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

	public Agenda createOfficialAgenda() {
		Agenda agenda = Agenda.builder()
			.agendaKey(UUID.randomUUID())
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(2)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(5)
			.status(AgendaStatus.ON_GOING)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createNonOfficialAgenda() {
		Agenda agenda = Agenda.builder()
			.agendaKey(UUID.randomUUID())
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(2)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(5)
			.status(AgendaStatus.ON_GOING)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public List<Agenda> createOfficialAgendaList(int size, AgendaStatus status) {
		List<Agenda> agendas = IntStream.range(0, size).mapToObj(i -> Agenda.builder()
				.agendaKey(UUID.randomUUID())
				.title("title " + UUID.randomUUID())
				.content("content " + UUID.randomUUID())
				.deadline(LocalDateTime.now().plusDays(i + 3))
				.startTime(LocalDateTime.now().plusDays(i + 5))
				.endTime(LocalDateTime.now().plusDays(i + 6))
				.minTeam(2)
				.maxTeam(5)
				.currentTeam(0)
				.minPeople(1)
				.maxPeople(5)
				.status(status)
				.posterUri("posterUri")
				.hostIntraId("hostIntraId")
				.location(Location.MIX)
				.isOfficial(true)	// true
				.isRanking(true)
				.build()
			)
			.collect(Collectors.toList());
		return agendaRepository.saveAll(agendas);
	}

	public List<Agenda> createNonOfficialAgendaList(int size, AgendaStatus status) {
		List<Agenda> agendas = IntStream.range(0, size).mapToObj(i -> Agenda.builder()
				.agendaKey(UUID.randomUUID())
				.title("title " + UUID.randomUUID())
				.content("content " + UUID.randomUUID())
				.deadline(LocalDateTime.now().plusDays(i + 3))
				.startTime(LocalDateTime.now().plusDays(i + 5))
				.endTime(LocalDateTime.now().plusDays(i + 6))
				.minTeam(2)
				.maxTeam(5)
				.currentTeam(0)
				.minPeople(1)
				.maxPeople(5)
				.status(status)
				.posterUri("posterUri")
				.hostIntraId("hostIntraId")
				.location(Location.MIX)
				.isOfficial(false)	// false
				.isRanking(true)
				.build()
			)
			.collect(Collectors.toList());
		return agendaRepository.saveAll(agendas);
	}

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
}
