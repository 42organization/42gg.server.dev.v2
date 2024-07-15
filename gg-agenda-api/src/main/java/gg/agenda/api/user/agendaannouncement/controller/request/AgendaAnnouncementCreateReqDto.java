package gg.agenda.api.user.agendaannouncement.controller.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAnnouncementCreateReqDto {

	@NotNull
	@NotEmpty
	private String title;

	@NotNull
	private String content;

	@Builder
	public AgendaAnnouncementCreateReqDto(String title, String content) {
		this.title = title;
		this.content = content;
	}

	@Mapper
	public interface MapStruct {

		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "title", source = "dto.title")
		@Mapping(target = "content", source = "dto.content")
		@Mapping(target = "isShow", constant = "true")
		@Mapping(target = "agenda", source = "agenda")
		AgendaAnnouncement toEntity(AgendaAnnouncementCreateReqDto dto, Agenda agenda);
	}
}
