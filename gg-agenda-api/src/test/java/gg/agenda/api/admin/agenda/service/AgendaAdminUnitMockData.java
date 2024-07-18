package gg.agenda.api.admin.agenda.service;

import java.time.LocalDateTime;
import java.util.List;

import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;

public class AgendaAdminUnitMockData {

	public static Agenda createMockAgenda(List<AgendaTeam> teams) {
		return Agenda.builder()
			.title("title")
			.content("content")
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.deadline(LocalDateTime.now().minusDays(3))
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(3))
			.isOfficial(true)
			.isRanking(true)
			.minTeam(5)
			.maxTeam(10)
			.currentTeam(teams.size())
			.minPeople(1)
			.maxPeople(10)
			.location(Location.SEOUL)
			.status(AgendaStatus.ON_GOING)
			.build();
	}

	public static Agenda createMockAgendaWithLocation(List<AgendaTeam> teams, Location location) {
		return Agenda.builder()
			.title("title")
			.content("content")
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.deadline(LocalDateTime.now().minusDays(3))
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(3))
			.isOfficial(true)
			.isRanking(true)
			.minTeam(5)
			.maxTeam(10)
			.currentTeam(teams.size())
			.minPeople(1)
			.maxPeople(10)
			.location(location)
			.status(AgendaStatus.ON_GOING)
			.build();
	}

	public static Agenda createMockAgendaWithAgendaCapacity(List<AgendaTeam> teams, int minTeam, int maxTeam) {
		return Agenda.builder()
			.title("title")
			.content("content")
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.deadline(LocalDateTime.now().minusDays(3))
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(3))
			.isOfficial(true)
			.isRanking(true)
			.minTeam(minTeam)
			.maxTeam(maxTeam)
			.currentTeam(teams.size())
			.minPeople(1)
			.maxPeople(10)
			.location(Location.SEOUL)
			.status(AgendaStatus.ON_GOING)
			.build();
	}

	public static Agenda createMockAgendaWithAgendaTeamCapacity(List<AgendaTeam> teams, int minPeople, int maxPeople) {
		return Agenda.builder()
			.title("title")
			.content("content")
			.posterUri("posterUri")
			.hostIntraId("hostIntraId")
			.deadline(LocalDateTime.now().minusDays(3))
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(3))
			.isOfficial(true)
			.isRanking(true)
			.minTeam(5)
			.maxTeam(10)
			.currentTeam(teams.size())
			.minPeople(minPeople)
			.maxPeople(maxPeople)
			.location(Location.SEOUL)
			.status(AgendaStatus.ON_GOING)
			.build();
	}

	public static AgendaAdminUpdateReqDto createMockAgendaUpdateReqDto() {
		return AgendaAdminUpdateReqDto.builder()
			.agendaTitle("Updated title")
			.agendaContents("Updated content")
			.agendaPoster("Updated posterUri")
			.isOfficial(false)
			.isRanking(false)
			.agendaStatus(AgendaStatus.CONFIRM)
			.agendaDeadLine(LocalDateTime.now())
			.agendaStartTime(LocalDateTime.now().plusDays(3))
			.agendaEndTime(LocalDateTime.now().plusDays(5))
			.agendaLocation(Location.MIX)
			.agendaMinTeam(2)
			.agendaMaxTeam(20)
			.agendaMinPeople(2)
			.agendaMaxPeople(20)
			.build();
	}

	public static AgendaAdminUpdateReqDto createMockAgendaUpdateReqDtoWithLocation(Location location) {
		return AgendaAdminUpdateReqDto.builder()
			.agendaTitle("Updated title")
			.agendaContents("Updated content")
			.agendaPoster("Updated posterUri")
			.isOfficial(false)
			.isRanking(false)
			.agendaStatus(AgendaStatus.CONFIRM)
			.agendaDeadLine(LocalDateTime.now())
			.agendaStartTime(LocalDateTime.now().plusDays(3))
			.agendaEndTime(LocalDateTime.now().plusDays(5))
			.agendaLocation(location)
			.agendaMinTeam(2)
			.agendaMaxTeam(20)
			.agendaMinPeople(2)
			.agendaMaxPeople(20)
			.build();
	}

	public static AgendaAdminUpdateReqDto createMockAgendaUpdateReqDtoWithAgendaCapacity(int minTeam, int maxTeam) {
		return AgendaAdminUpdateReqDto.builder()
			.agendaTitle("Updated title")
			.agendaContents("Updated content")
			.agendaPoster("Updated posterUri")
			.isOfficial(false)
			.isRanking(false)
			.agendaStatus(AgendaStatus.CONFIRM)
			.agendaDeadLine(LocalDateTime.now())
			.agendaStartTime(LocalDateTime.now().plusDays(3))
			.agendaEndTime(LocalDateTime.now().plusDays(5))
			.agendaLocation(Location.SEOUL)
			.agendaMinTeam(minTeam)
			.agendaMaxTeam(maxTeam)
			.agendaMinPeople(2)
			.agendaMaxPeople(20)
			.build();
	}

	public static AgendaAdminUpdateReqDto createMockAgendaUpdateReqDtoWithAgendaTeamCapacity(
		int minPeople, int maxPeople) {
		return AgendaAdminUpdateReqDto.builder()
			.agendaTitle("Updated title")
			.agendaContents("Updated content")
			.agendaPoster("Updated posterUri")
			.isOfficial(false)
			.isRanking(false)
			.agendaStatus(AgendaStatus.CONFIRM)
			.agendaDeadLine(LocalDateTime.now())
			.agendaStartTime(LocalDateTime.now().plusDays(3))
			.agendaEndTime(LocalDateTime.now().plusDays(5))
			.agendaLocation(Location.SEOUL)
			.agendaMinTeam(2)
			.agendaMaxTeam(20)
			.agendaMinPeople(minPeople)
			.agendaMaxPeople(maxPeople)
			.build();
	}
}
