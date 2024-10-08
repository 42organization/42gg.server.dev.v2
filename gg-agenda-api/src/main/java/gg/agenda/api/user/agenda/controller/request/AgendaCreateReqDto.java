package gg.agenda.api.user.agenda.controller.request;

import java.net.URL;
import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;

import gg.agenda.api.user.agenda.controller.request.validator.AgendaCapacityValid;
import gg.agenda.api.user.agenda.controller.request.validator.AgendaScheduleValid;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AgendaCapacityValid
@AgendaScheduleValid
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaCreateReqDto {

	@NotBlank
	private String agendaTitle;

	@NotBlank
	private String agendaContent;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Future(message = "마감일은 현재 시간 이후여야 합니다.")
	private LocalDateTime agendaDeadLine;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
	private LocalDateTime agendaStartTime;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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

	private URL agendaPosterUri;

	@NotNull
	private Location agendaLocation;

	@NotNull
	private Boolean agendaIsRanking;

	@Builder
	public AgendaCreateReqDto(String agendaTitle, String agendaContent, LocalDateTime agendaDeadLine,
		LocalDateTime agendaStartTime, LocalDateTime agendaEndTime, int agendaMinTeam, int agendaMaxTeam,
		int agendaMinPeople, int agendaMaxPeople, URL agendaPosterUri, Location agendaLocation,
		Boolean agendaIsRanking) {
		this.agendaTitle = agendaTitle;
		this.agendaContent = agendaContent;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaPosterUri = agendaPosterUri;
		this.agendaLocation = agendaLocation;
		this.agendaIsRanking = agendaIsRanking;
	}

	public void updatePosterUri(URL storedUrl) {
		this.agendaPosterUri = storedUrl;
	}

	@Mapper
	public interface MapStruct {

		AgendaCreateReqDto.MapStruct INSTANCE = Mappers.getMapper(AgendaCreateReqDto.MapStruct.class);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "title", source = "dto.agendaTitle")
		@Mapping(target = "content", source = "dto.agendaContent")
		@Mapping(target = "deadline", source = "dto.agendaDeadLine")
		@Mapping(target = "startTime", source = "dto.agendaStartTime")
		@Mapping(target = "endTime", source = "dto.agendaEndTime")
		@Mapping(target = "minTeam", source = "dto.agendaMinTeam")
		@Mapping(target = "maxTeam", source = "dto.agendaMaxTeam")
		@Mapping(target = "currentTeam", constant = "0")
		@Mapping(target = "minPeople", source = "dto.agendaMinPeople")
		@Mapping(target = "maxPeople", source = "dto.agendaMaxPeople")
		@Mapping(target = "hostIntraId", source = "userIntraId")
		@Mapping(target = "location", source = "dto.agendaLocation")
		@Mapping(target = "isRanking", source = "dto.agendaIsRanking")
		@Mapping(target = "status", constant = "OPEN")
		@Mapping(target = "isOfficial", constant = "false")
		@Mapping(target = "posterUri", source = "dto.agendaPosterUri")
		Agenda toEntity(AgendaCreateReqDto dto, String userIntraId);
	}
}
