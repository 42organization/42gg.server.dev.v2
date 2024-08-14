package gg.agenda.api.admin.agenda.controller.response;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAdminSimpleResDto {

	UUID agendaKey;

	String agendaTitle;

	@Builder
	public AgendaAdminSimpleResDto(UUID agendaKey, String agendaTitle) {
		this.agendaKey = agendaKey;
		this.agendaTitle = agendaTitle;
	}

	@Mapper
	public interface MapStruct {

		AgendaAdminSimpleResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaAdminSimpleResDto.MapStruct.class);

		@Mapping(target = "agendaKey", source = "agendaKey")
		@Mapping(target = "agendaTitle", source = "title")
		AgendaAdminSimpleResDto toDto(Agenda agenda);
	}
}
