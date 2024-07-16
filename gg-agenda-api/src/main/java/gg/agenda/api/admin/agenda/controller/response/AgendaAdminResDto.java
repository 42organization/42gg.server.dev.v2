package gg.agenda.api.admin.agenda.controller.response;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAdminResDto {

	private Long agendaId;

	private String agendaTitle;

	private String agendaDeadLine;

	private String agendaStartTime;

	private String agendaEndTime;

	private int agendaCurrentTeam;

	private int agendaMaxTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private Location agendaLocation;

	private Boolean isRanking;

	private Boolean isOfficial;

	private AgendaStatus agendaStatus;

	@Builder
	public AgendaAdminResDto(Long agendaId, String agendaTitle, String agendaDeadLine, String agendaStartTime,
		String agendaEndTime, int agendaCurrentTeam, int agendaMaxTeam, int agendaMinPeople, int agendaMaxPeople,
		Location agendaLocation, Boolean isRanking, Boolean isOfficial, AgendaStatus agendaStatus) {
		this.agendaId = agendaId;
		this.agendaTitle = agendaTitle;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaCurrentTeam = agendaCurrentTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaLocation = agendaLocation;
		this.isRanking = isRanking;
		this.isOfficial = isOfficial;
		this.agendaStatus = agendaStatus;
	}

	@Mapper
	public interface MapStruct {

		AgendaAdminResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaAdminResDto.MapStruct.class);

		@Mapping(target = "agendaId", source = "id")
		@Mapping(target = "agendaTitle", source = "title")
		@Mapping(target = "agendaDeadLine", source = "deadLine")
		@Mapping(target = "agendaStartTime", source = "startTime")
		@Mapping(target = "agendaEndTime", source = "endTime")
		@Mapping(target = "agendaCurrentTeam", source = "currentTeam")
		@Mapping(target = "agendaMaxTeam", source = "maxTeam")
		@Mapping(target = "agendaMinPeople", source = "minPeople")
		@Mapping(target = "agendaMaxPeople", source = "maxPeople")
		@Mapping(target = "agendaLocation", source = "location")
		@Mapping(target = "isRanking", source = "ranking")
		@Mapping(target = "isOfficial", source = "official")
		@Mapping(target = "agendaStatus", source = "status")
		AgendaAdminResDto toAgendaAdminResDto(Agenda agenda);
	}
}
