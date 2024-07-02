package gg.agenda.api;

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
public class AgendaMockData {

	private final AgendaRepository agendaRepository;

	public Agenda createAgenda() {
		Agenda agenda = Agenda.builder()
			.key(UUID.randomUUID())
			.title("title")
			.content("content")
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
}
