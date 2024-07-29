package gg.agenda.api.admin.agendateam.controller.response;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaProfileResDto {

	private String intraId;

	private Coalition coalition;

	@Builder
	public AgendaProfileResDto(String intraId, Coalition coalition) {
		this.intraId = intraId;
		this.coalition = coalition;
	}

	@Mapper
	public interface MapStruct {

		AgendaProfileResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaProfileResDto.MapStruct.class);

		@Mapping(target = "intraId", source = "intraId")
		@Mapping(target = "coalition", source = "coalition")
		AgendaProfileResDto toDto(AgendaProfile profile);
	}
}
