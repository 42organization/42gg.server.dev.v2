package gg.utils.fixture.agenda;

import static gg.data.agenda.type.Coalition.*;

import org.springframework.stereotype.Component;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaProfileFixture {
	private final AgendaProfileRepository agendaProfileRepository;

	public AgendaProfile createAgendaProfile(User user, Location location) {
		AgendaProfile agendaProfile = AgendaProfile.builder()
			.content("content")
			.githubUrl("githubUrl")
			.coalition(LEE)
			.location(location)
			.intraId(user.getIntraId())
			.userId(user.getId())
			.build();
		return agendaProfileRepository.save(agendaProfile);
	}
}
