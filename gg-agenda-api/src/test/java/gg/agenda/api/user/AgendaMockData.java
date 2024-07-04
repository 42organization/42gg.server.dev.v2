package gg.agenda.api.user;

import static gg.data.agenda.type.AgendaStatus.*;
import static gg.data.agenda.type.Coalition.*;
import static gg.data.agenda.type.Location.*;
import static java.util.UUID.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaMockData {
	private final AgendaRepository agendaRepository;
	private final TicketRepository ticketRepository;
	private final AgendaTeamRepository agendaTeamRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	public Agenda createAgenda() {
		UUID uuid = randomUUID();
		Agenda agenda = Agenda.builder()
			.agendaKey(uuid)
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
		UUID uuid = randomUUID();
		Agenda agenda = Agenda.builder()
			.agendaKey(uuid)
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
		UUID uuid = randomUUID();
		Agenda agenda = Agenda.builder()
			.agendaKey(uuid)
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
		UUID uuid = randomUUID();
		Agenda agenda = Agenda.builder()
			.agendaKey(uuid)
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
		UUID uuid = randomUUID();
		Agenda agenda = Agenda.builder()
			.agendaKey(uuid)
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
			.coalition(String.valueOf(LEE))
			.location(String.valueOf(location))
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
}
