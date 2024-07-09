package gg.agenda.api;

import static gg.data.agenda.type.AgendaStatus.*;
import static gg.data.agenda.type.AgendaStatus.CONFIRM;
import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.data.agenda.type.Coalition.*;
import static gg.data.agenda.type.Location.*;
import static java.util.UUID.*;

import java.time.LocalDateTime;
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
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
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
			.status(AgendaStatus.ON_GOING)
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
			.status(AgendaStatus.ON_GOING)
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
				.status(CONFIRM)
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
			.status(ON_GOING)
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
			.status(ON_GOING)
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
			.minTeam(1)
			.maxTeam(5)
			.currentTeam(0)
			.minPeople(1)
			.maxPeople(3)
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.location(location)
			.status(ON_GOING)
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
			.status(ON_GOING)
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
			.userId(user.getId())
			.build();
		return agendaProfileRepository.save(agendaProfile);
	}

	public Ticket createTicket(AgendaProfile agendaProfile) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(agendaProfile)
			.isApprove(true)
			.isUsed(false)
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
			.status(OPEN)
			.location(SEOUL)
			.mateCount(3)
			.award("award")
			.awardPriority(1)
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
			.status(OPEN)
			.location(SEOUL)
			.mateCount(3)
			.award("award")
			.awardPriority(1)
			.isPrivate(false)
			.build();
		return agendaTeamRepository.save(agendaTeam);
	}

	public AgendaTeamProfile createAgendaTeamProfile(AgendaTeam agendaTeam, AgendaProfile agendaProfile) {
		AgendaTeamProfile agendaTeamProfile = AgendaTeamProfile.builder()
			.agendaTeam(agendaTeam)
			.profile(agendaProfile)
			.isExist(true)
			.build();
		return agendaTeamProfileRepository.save(agendaTeamProfile);
	}
}
