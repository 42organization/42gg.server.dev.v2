package gg.utils.fixture.agenda;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.Location;
import gg.repo.agenda.AgendaRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static gg.data.agenda.type.AgendaStatus.*;

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
			.maxPeople(5)
			.status(ON_GOING)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(Location.MIX)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}
}
