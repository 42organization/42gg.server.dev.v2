package gg.agenda.api.admin.agendateam.controller.response;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamMateResDto {

	private String intraId;

	private Coalition coalition;

	@Builder
	public AgendaTeamMateResDto(String intraId, Coalition coalition) {
		this.intraId = intraId;
		this.coalition = coalition;
	}

	@Mapper
	public interface MapStruct {

		AgendaTeamMateResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaTeamMateResDto.MapStruct.class);

		@Mapping(target = "intraId", source = "intraId")
		@Mapping(target = "coalition", source = "coalition")
		AgendaTeamMateResDto toDto(AgendaProfile profile);
	}
}
