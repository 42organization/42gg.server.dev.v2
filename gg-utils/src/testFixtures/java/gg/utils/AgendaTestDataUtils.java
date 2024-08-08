package gg.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.utils.fixture.agenda.AgendaAnnouncementFixture;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import gg.utils.fixture.agenda.AgendaTeamProfileFixture;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaTestDataUtils {

	private final AgendaFixture agendaFixture;

	private final AgendaAnnouncementFixture agendaAnnouncementFixture;

	private final AgendaTeamFixture agendaTeamFixture;

	private final AgendaProfileFixture agendaProfileFixture;

	private final AgendaTeamProfileFixture agendaTeamProfileFixture;

	@PersistenceContext
	private final EntityManager em;

	public Agenda createAgendaAndAnnouncements(int size) {
		Agenda agenda = agendaFixture.createAgenda();
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size / 2, true);
		agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, size - size / 2, false);
		return agenda;
	}

	public Agenda createAgendaAndAgendaTeams(String intraId, int size, AgendaStatus status) {
		Agenda agenda = agendaFixture.createAgenda(intraId, status);
		agendaTeamFixture.createAgendaTeamList(agenda, AgendaTeamStatus.CONFIRM, size);
		return agenda;
	}

	public Agenda createAgendaTeamProfiles(User user, AgendaStatus status) {
		AgendaProfile host = agendaProfileFixture.createAgendaProfile(user, Location.SEOUL);
		Agenda agenda = agendaFixture.createAgenda(host.getIntraId(), status);
		for(int i = 0; i < 3; i++) {
			AgendaProfile leader = agendaProfileFixture.createAgendaProfile();
			List<AgendaProfile> mates = agendaProfileFixture.createAgendaProfileList(3);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, leader, AgendaTeamStatus.OPEN);
			agendaTeamProfileFixture.createAgendaTeamProfileList(agenda, team, mates);
		}
		for(int i = 0; i < 3; i++) {
			AgendaProfile leader = agendaProfileFixture.createAgendaProfile();
			List<AgendaProfile> mates = agendaProfileFixture.createAgendaProfileList(3);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, leader, AgendaTeamStatus.OPEN);
			agendaTeamProfileFixture.createAgendaTeamProfileList(agenda, team, mates);
			team.confirm();
		}
		for(int i = 0; i < 3; i++) {
			AgendaProfile leader = agendaProfileFixture.createAgendaProfile();
			List<AgendaProfile> mates = agendaProfileFixture.createAgendaProfileList(3);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, leader, AgendaTeamStatus.OPEN);
			agendaTeamProfileFixture.createAgendaTeamProfileList(agenda, team, mates);
			team.cancelTeam();
		}
		return agenda;
	}
}
