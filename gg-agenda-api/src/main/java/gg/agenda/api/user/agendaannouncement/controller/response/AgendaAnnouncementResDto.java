package gg.agenda.api.user.agendaannouncement.controller.response;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.AgendaAnnouncement;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAnnouncementResDto {

	long id;

	String title;

	String content;

	LocalDateTime createdAt;

	@Builder
	public AgendaAnnouncementResDto(long id, String title, String content, LocalDateTime createdAt) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		AgendaAnnouncementResDto toDto(AgendaAnnouncement announcement);
	}
}
