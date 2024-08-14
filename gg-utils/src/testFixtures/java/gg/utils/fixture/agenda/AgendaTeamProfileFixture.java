package gg.utils.fixture.agenda;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.repo.agenda.AgendaTeamProfileRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaTeamProfileFixture {
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	public AgendaTeamProfile createAgendaTeamProfile(Agenda agenda, AgendaTeam agendaTeam,
		AgendaProfile agendaProfile) {
		AgendaTeamProfile agendaTeamProfile = AgendaTeamProfile.builder()
			.agenda(agenda)
			.agendaTeam(agendaTeam)
			.profile(agendaProfile)
			.isExist(true)
			.build();
		agendaTeam.attendTeam(agenda);
		return agendaTeamProfileRepository.save(agendaTeamProfile);
	}

	public AgendaTeamProfile createAgendaTeamProfile(AgendaTeam team, AgendaProfile seoulUserAgendaProfile) {
		AgendaTeamProfile agendaTeamProfile = AgendaTeamProfile.builder()
			.agenda(team.getAgenda())
			.agendaTeam(team)
			.profile(seoulUserAgendaProfile)
			.isExist(true)
			.build();
		return agendaTeamProfileRepository.save(agendaTeamProfile);
	}

	public List<AgendaTeamProfile> createAgendaTeamProfileList(Agenda agenda,
		AgendaTeam team, List<AgendaProfile> mates) {
		return mates.stream()
			.map(mate -> createAgendaTeamProfile(agenda, team, mate))
			.collect(Collectors.toList());
	}
}
