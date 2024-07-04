package gg.agenda.api.user.agenda.controller.dto;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.Location;
import gg.utils.exception.custom.InvalidParameterException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaCreateDto {

	@NotNull
	@NotEmpty
	private String agendaTitle;

	@NotNull
	@NotEmpty
	private String agendaContents;

	@NotNull
	@Future(message = "마감일은 현재 시간 이후여야 합니다.")
	private LocalDateTime agendaDeadLine;

	@NotNull
	@Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
	private LocalDateTime agendaStartTime;

	@NotNull
	@Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
	private LocalDateTime agendaEndTime;

	@Min(2)
	@Max(1000)
	private int agendaMinTeam;

	@Min(2)
	@Max(1000)
	private int agendaMaxTeam;

	@Min(1)
	@Max(100)
	private int agendaMinPeople;

	@Min(1)
	@Max(100)
	private int agendaMaxPeople;

	private String agendaPoster;

	@NotNull
	private Location agendaLocation;

	@NotNull
	private Boolean agendaIsRanking;

	@NotNull
	private Boolean agendaIsOfficial;

	@Builder
	public AgendaCreateDto(String agendaTitle, String agendaContents, LocalDateTime agendaDeadLine,
		LocalDateTime agendaStartTime, LocalDateTime agendaEndTime, int agendaMinTeam, int agendaMaxTeam,
		int agendaMinPeople, int agendaMaxPeople, String agendaPoster, Location agendaLocation,
		Boolean agendaIsRanking, Boolean agendaIsOfficial) {
		this.agendaTitle = agendaTitle;
		this.agendaContents = agendaContents;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaPoster = agendaPoster;
		this.agendaLocation = agendaLocation;
		this.agendaIsRanking = agendaIsRanking;
		this.agendaIsOfficial = agendaIsOfficial;
	}

	@Mapper
	public interface MapStruct {
		AgendaCreateDto.MapStruct INSTANCE = Mappers.getMapper(AgendaCreateDto.MapStruct.class);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "title", source = "dto.agendaTitle")
		@Mapping(target = "content", source = "dto.agendaContents")
		@Mapping(target = "deadline", source = "dto.agendaDeadLine")
		@Mapping(target = "startTime", source = "dto.agendaStartTime")
		@Mapping(target = "endTime", source = "dto.agendaEndTime")
		@Mapping(target = "minTeam", source = "dto.agendaMinTeam")
		@Mapping(target = "maxTeam", source = "dto.agendaMaxTeam")
		@Mapping(target = "currentTeam", constant = "0")
		@Mapping(target = "minPeople", source = "dto.agendaMinPeople")
		@Mapping(target = "maxPeople", source = "dto.agendaMaxPeople")
		@Mapping(target = "posterUri", source = "dto.agendaPoster")
		@Mapping(target = "hostIntraId", source = "user.intraId")
		@Mapping(target = "location", source = "dto.agendaLocation")
		@Mapping(target = "status", constant = "ON_GOING")
		@Mapping(target = "isOfficial", source = "dto.agendaIsOfficial")
		@Mapping(target = "isRanking", source = "dto.agendaIsRanking")
		Agenda toEntity(AgendaCreateDto dto, UserDto user);

		@BeforeMapping
		default void mustHaveValidAgendaSchedule(AgendaCreateDto dto, UserDto user, @MappingTarget Agenda agenda) {
			if (!dto.getAgendaDeadLine().isBefore(dto.getAgendaStartTime())) {
				throw new InvalidParameterException(AGENDA_INVALID_SCHEDULE);
			}
			if (!dto.getAgendaDeadLine().isBefore(dto.getAgendaEndTime())) {
				throw new InvalidParameterException(AGENDA_INVALID_SCHEDULE);
			}
			if (!dto.getAgendaStartTime().isBefore(dto.getAgendaEndTime())) {
				throw new InvalidParameterException(AGENDA_INVALID_SCHEDULE);
			}

		}

		@BeforeMapping
		default void mustHaveValidParam(AgendaCreateDto dto, UserDto user, @MappingTarget Agenda agenda) {
			if (dto.getAgendaMinTeam() > dto.getAgendaMaxTeam()) {
				throw new InvalidParameterException(AGENDA_INVALID_PARAM);
			}
			if (dto.getAgendaMinPeople() > dto.getAgendaMaxPeople()) {
				throw new InvalidParameterException(AGENDA_INVALID_PARAM);

			}
		}
	}
}
