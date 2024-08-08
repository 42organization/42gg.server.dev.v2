package gg.utils.fixture.agenda;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.data.agenda.type.Location.*;
import static java.util.UUID.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaTeamFixture {

	private final TestDataUtils testDataUtils;

	private final AgendaProfileFixture agendaProfileFixture;

	private final AgendaTeamProfileFixture agendaTeamProfileFixture;

	private final AgendaTeamRepository agendaTeamRepository;

	@PersistenceContext
	private final EntityManager em;

	public AgendaTeam createAgendaTeam(Agenda agenda) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(OPEN)
			.location(MIX)
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, AgendaProfile profile) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(profile.getIntraId())
			.status(OPEN)
			.location(MIX)
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, AgendaProfile profile, AgendaTeamStatus status) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(profile.getIntraId())
			.status(status)
			.location(MIX)
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		AgendaTeam savedTeam = agendaTeamRepository.save(agendaTeam);
		agendaTeamProfileFixture.createAgendaTeamProfile(agenda, savedTeam, profile);
		return savedTeam;
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(OPEN)
			.location(MIX)
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, Location location) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(OPEN)
			.location(location)
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(int mateCount, Agenda agenda, User seoulUser, Location location) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(seoulUser.getIntraId())
			.status(OPEN)
			.location(location)
			.mateCount(mateCount)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, Location location, AgendaTeamStatus agendaTeamStatus) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(agendaTeamStatus)
			.location(location)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user, Location location) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(OPEN)
			.location(location)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user, Location location, AgendaTeamStatus status) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(status)
			.location(location)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public List<AgendaTeam> createAgendaTeamList(Agenda agenda, AgendaTeamStatus status, int size) {
		List<AgendaTeam> teams = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			AgendaTeam agendaTeam = AgendaTeam.builder()
				.agenda(agenda)
				.teamKey(randomUUID())
				.name("name")
				.content("content")
				.leaderIntraId("intraId" + i)
				.status(status)
				.location(SEOUL)
				.mateCount(3)
				.awardPriority(1)
				.isPrivate(false)
				.build();
			teams.add(agendaTeam);
			if (status == CONFIRM) {
				agenda.confirmTeam(agendaTeam.getLocation(), LocalDateTime.now());
			}
		}
		return agendaTeamRepository.saveAll(teams);
	}
}
