package gg.utils.fixture.agenda;

import static gg.data.agenda.type.AgendaStatus.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.repo.agenda.AgendaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaFixture {

	private final AgendaRepository agendaRepository;

	public Agenda createAgenda() {
		Agenda agenda = Agenda.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(2)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(6)
			.status(OPEN)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(int minTeam, int maxTeam, int minPeople, int maxPeople) {
		Agenda agenda = Agenda.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(minTeam)
			.maxTeam(maxTeam)
			.currentTeam(0)
			.minPeople(minPeople)
			.maxPeople(maxPeople)
			.status(OPEN)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(String intraId, AgendaStatus status) {
		Agenda agenda = Agenda.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(2)
			.maxTeam(20)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(10)
			.status(status)
			.posterUri("posterUri")
			.hostIntraId(intraId)
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(Location location) {
		Agenda agenda = Agenda.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(LocalDateTime.now().plusDays(3))
			.startTime(LocalDateTime.now().plusDays(5))
			.endTime(LocalDateTime.now().plusDays(6))
			.minTeam(2)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(6)
			.status(OPEN)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(location)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(LocalDateTime localDateTime) {
		Agenda agenda = Agenda.builder()
			.title("title " + UUID.randomUUID())
			.content("content " + UUID.randomUUID())
			.deadline(localDateTime)
			.startTime(localDateTime.plusDays(2))
			.endTime(localDateTime.plusDays(3))
			.minTeam(2)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(5)
			.status(OPEN)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(AgendaStatus agendaStatus) {
		Agenda agenda = Agenda.builder()
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
			.status(agendaStatus)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}
}
