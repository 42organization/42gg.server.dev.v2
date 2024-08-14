package gg.agenda.api;

import static gg.data.agenda.type.AgendaStatus.*;
import static gg.data.agenda.type.Coalition.*;
import static gg.data.agenda.type.Location.*;
import static java.util.UUID.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaMockData {

	private final EntityManager em;
	private final TicketRepository ticketRepository;
	private final AgendaRepository agendaRepository;
	private final AgendaTeamRepository agendaTeamRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;
	private final AgendaAnnouncementRepository agendaAnnouncementRepository;
	private final TestDataUtils testDataUtils;

	public Agenda createOfficialAgenda() {
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
			.status(AgendaStatus.OPEN)
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
			.status(AgendaStatus.OPEN)
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
				.isOfficial(true)    // true
				.isRanking(true)
				.build()
			)
			.collect(Collectors.toList());
		return agendaRepository.saveAll(agendas);
	}

	public List<Agenda> createNonOfficialAgendaList(int size, AgendaStatus status) {
		List<Agenda> agendas = IntStream.range(0, size).mapToObj(i -> Agenda.builder()
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
				.isOfficial(false)    // false
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

	public List<AgendaAnnouncement> createAgendaAnnouncementList(Agenda agenda, int size) {
		List<AgendaAnnouncement> announcements = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			announcements.add(AgendaAnnouncement.builder()
				.title("title " + i)
				.content("content " + i)
				.isShow(true)
				.agenda(agenda)
				.build());
		}
		return agendaAnnouncementRepository.saveAll(announcements);
	}

	public List<AgendaAnnouncement> createAgendaAnnouncementList(Agenda agenda, int size, boolean isShow) {
		List<AgendaAnnouncement> announcements = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			announcements.add(AgendaAnnouncement.builder()
				.title("title " + i)
				.content("content " + i)
				.isShow(isShow)
				.agenda(agenda)
				.build());
		}
		return agendaAnnouncementRepository.saveAll(announcements);
	}

	public List<Agenda> createAgendaHistory(int size) {
		List<Agenda> agendas = IntStream.range(0, size).mapToObj(i -> Agenda.builder()
				.title("title " + UUID.randomUUID())
				.content("content " + UUID.randomUUID())
				.deadline(LocalDateTime.now().minusDays(i + 6))
				.startTime(LocalDateTime.now().minusDays(i + 4))
				.endTime(LocalDateTime.now().minusDays(i + 2))
				.minTeam(2)
				.maxTeam(5)
				.currentTeam(0)
				.minPeople(1)
				.maxPeople(5)
				.status(FINISH)
				.posterUri("posterUri")
				.hostIntraId("hostIntraId")
				.location(Location.MIX)
				.isOfficial(true)
				.isRanking(true)
				.build()
			)
			.collect(Collectors.toList());
		return agendaRepository.saveAll(agendas);
	}

	public Agenda createAgenda() {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(String intraId) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId(intraId)
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(String intraId, LocalDateTime startTime, boolean rank) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(startTime.minusDays(1))
			.startTime(startTime)
			.endTime(startTime.plusDays(1))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId(intraId)
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(rank)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(String intraId, LocalDateTime startTime, boolean rank, AgendaStatus status) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(startTime.minusDays(1))
			.startTime(startTime)
			.endTime(startTime.plusDays(1))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId(intraId)
			.location(SEOUL)
			.status(status)
			.isOfficial(true)
			.isRanking(rank)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(String intraId, LocalDateTime startTime, AgendaStatus status) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(startTime.minusDays(1))
			.startTime(startTime)
			.endTime(startTime.plusDays(1))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId(intraId)
			.location(SEOUL)
			.status(status)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(LocalDateTime deadline) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(deadline)
			.startTime(deadline.plusDays(1))
			.endTime(deadline.plusDays(2))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgendaWithAgendaCapacity(int min, int max) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(min)
			.maxTeam(max)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgendaWithAgendaCapacityAndStatus(int min, int max, AgendaStatus status) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(min)
			.maxTeam(max)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(status)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgendaWithAgendaTeamCapacity(int min, int max) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(2)
			.maxTeam(10)
			.currentTeam(0)
			.minPeople(min)
			.maxPeople(max)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(Location location) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(2)
			.maxTeam(20)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(20)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(location)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(int curruentTeam) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(curruentTeam)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createNeedMorePeopleAgenda(int curruentTeam) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(curruentTeam)
			.minPeople(3)
			.maxPeople(5)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(AgendaStatus.OPEN)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

	public Agenda createAgenda(AgendaStatus status) {
		Agenda agenda = Agenda.builder()
			.title("title")
			.content("content")
			.deadline(LocalDateTime.now().plusDays(1))
			.startTime(LocalDateTime.now().plusDays(2))
			.endTime(LocalDateTime.now().plusDays(3))
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(SEOUL)
			.status(status)
			.isOfficial(true)
			.isRanking(true)
			.build();
		return agendaRepository.save(agenda);
	}

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

	public Ticket createTicket(AgendaProfile agendaProfile) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(true)
			.approvedAt(LocalDateTime.now().minusDays(1))
			.isUsed(false)
			.usedAt(null)
			.build();
		return ticketRepository.save(ticket);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(AgendaTeamStatus.OPEN)
			.location(SEOUL)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public List<AgendaTeam> createAgendaTeamList(Agenda agenda, int size, AgendaTeamStatus status) {
		List<AgendaTeam> agendaTeams = IntStream.range(0, size).mapToObj(i -> AgendaTeam.builder()
				.agenda(agenda)
				.teamKey(randomUUID())
				.name("name")
				.content("content")
				.leaderIntraId("leaderIntraId")
				.status(status)
				.location(SEOUL)
				.mateCount(3)
				.awardPriority(1)
				.isPrivate(false)
				.build()
			)
			.collect(Collectors.toList());
		return agendaTeamRepository.saveAll(agendaTeams);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, String teamName) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name(teamName)
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(AgendaTeamStatus.OPEN)
			.location(SEOUL)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, String teamName, AgendaTeamStatus status) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name(teamName)
			.content("content")
			.leaderIntraId("leaderIntraId")
			.status(status)
			.location(SEOUL)
			.mateCount(3)
			.awardPriority(-1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(AgendaTeamStatus.OPEN)
			.location(SEOUL)
			.mateCount(3)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user, int mateCount) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(AgendaTeamStatus.OPEN)
			.location(SEOUL)
			.mateCount(mateCount)
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
			.status(AgendaTeamStatus.OPEN)
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

	public AgendaTeam createAgendaTeam(int currentTeam, Agenda agenda, User user, Location location) {
		AgendaTeam agendaTeam = AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(randomUUID())
			.name("name")
			.content("content")
			.leaderIntraId(user.getIntraId())
			.status(AgendaTeamStatus.OPEN)
			.location(location)
			.mateCount(currentTeam)
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeam createAgendaTeam(Agenda agenda, User user, Location location, AgendaTeamStatus status,
		Boolean isPrivate) {
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
			.isPrivate(isPrivate)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeamProfile createAgendaTeamProfile(AgendaTeam agendaTeam, AgendaProfile agendaProfile) {
		AgendaTeamProfile agendaTeamProfile = AgendaTeamProfile.builder()
			.agendaTeam(agendaTeam)
			.agenda(agendaTeam.getAgenda())
			.profile(agendaProfile)
			.isExist(true)
			.build();
		return agendaTeamProfileRepository.save(agendaTeamProfile);
	}

	public Agenda createAgendaWithTeam(int teamCount) {
		Agenda agenda = createAgenda(SEOUL);
		for (int i = 0; i < teamCount; i++) {
			AgendaTeam agendaTeam = createAgendaTeam(agenda);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		return agenda;
	}

	public Agenda createAgendaWithTeamGyeongsan(int teamCount) {
		Agenda agenda = createAgenda(GYEONGSAN);
		for (int i = 0; i < teamCount; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, GYEONGSAN);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		return agenda;
	}

	public Agenda createAgendaWithTeamMix(int teamCount) {
		Agenda agenda = createAgenda(MIX);
		int half = teamCount / 2;
		for (int i = 0; i < teamCount - half; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, SEOUL);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		for (int i = 0; i < half; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, GYEONGSAN);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		return agenda;
	}

	public Agenda createAgendaWithTeamAndAgendaCapacity(int teamCount, int min, int max) {
		Agenda agenda = createAgendaWithAgendaCapacity(min, max);
		for (int i = 0; i < teamCount; i++) {
			AgendaTeam agendaTeam = createAgendaTeam(agenda);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		return agenda;
	}

	public Agenda createAgendaWithTeamAndAgendaCapacityAndFinish(int teamCount, int min, int max) {
		Agenda agenda = createAgendaWithAgendaCapacity(min, max);
		for (int i = 0; i < teamCount; i++) {
			AgendaTeam agendaTeam = createAgendaTeam(agenda);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		agenda.updateSchedule(LocalDateTime.now().minusDays(2),
			LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
		agenda.confirmAgenda();
		agenda.finishAgenda();
		em.persist(agenda);
		em.flush();
		em.clear();
		return agenda;
	}

	public Agenda createAgendaWithTeamAndAgendaTeamCapacity(int teamCount, int min, int max) {
		Agenda agenda = createAgendaWithAgendaTeamCapacity(min, max);
		for (int i = 0; i < teamCount; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, 10);
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
		}
		return agenda;
	}

	public Agenda createAgendaWithTeamAndAgendaTeamCapacityAndFinish(int teamCount, int min, int max) {
		Agenda agenda = createAgendaWithAgendaCapacityAndStatus(min, max, CONFIRM);
		for (int i = 0; i < teamCount; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, 3);
			agendaTeam.confirm();
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
			em.persist(agendaTeam);
			em.flush();
			em.clear();
		}
		return agenda;
	}

	public Agenda createAgendaWithStatusAndTeamWithAgendaTeamCapacity(AgendaStatus status,
		int teamCount, int min, int max) {
		Agenda agenda = createAgendaWithAgendaCapacityAndStatus(min, max, status);
		for (int i = 0; i < teamCount; i++) {
			User user = testDataUtils.createNewUser();
			AgendaTeam agendaTeam = createAgendaTeam(agenda, user, 3);
			agendaTeam.confirm();
			agenda.addTeam(agendaTeam.getLocation(), LocalDateTime.now());
			em.persist(agendaTeam);
			em.flush();
			em.clear();
		}
		return agenda;
	}
}
