package gg.utils.fixture.agenda;

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
}
