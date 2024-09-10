package gg.utils.fixture.agenda;

import static gg.data.agenda.type.Coalition.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import gg.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaProfileFixture {

	private final AgendaProfileRepository agendaProfileRepository;

	private final TestDataUtils testDataUtils;

	public AgendaProfile createAgendaProfile(User user, Location location) {
		AgendaProfile agendaProfile = AgendaProfile.builder()
			.content("content")
			.githubUrl("githubUrl")
			.coalition(LEE)
			.location(location)
			.intraId(user.getIntraId())
			.userId(user.getId())
			.fortyTwoId(user.getId())
			.build();
		return agendaProfileRepository.save(agendaProfile);
	}

	public AgendaProfile createAgendaProfile() {
		User user = testDataUtils.createNewUser();
		AgendaProfile agendaProfile = AgendaProfile.builder()
			.content("content")
			.githubUrl("githubUrl")
			.coalition(LEE)
			.location(Location.SEOUL)
			.intraId(user.getIntraId())
			.userId(user.getId())
			.fortyTwoId(user.getId())
			.build();
		return agendaProfileRepository.save(agendaProfile);
	}

	public List<AgendaProfile> createAgendaProfileList(int size) {
		List<AgendaProfile> agendaProfileList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			User user = testDataUtils.createNewUser();
			AgendaProfile agendaProfile = AgendaProfile.builder()
				.content("content")
				.githubUrl("githubUrl")
				.coalition(LEE)
				.location(Location.SEOUL)
				.intraId(user.getIntraId())
				.userId(user.getId())
				.fortyTwoId(user.getId())
				.build();
			agendaProfileList.add(agendaProfile);
		}
		return agendaProfileRepository.saveAll(agendaProfileList);
	}
}
