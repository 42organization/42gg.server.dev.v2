package gg.agenda.api.admin.agendaannouncement.controller.response;

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
public class AgendaAnnouncementAdminResDto {

	private Long id;

	private String title;

	private String content;

	private Boolean isShow;

	private LocalDateTime createdAt;

	@Builder
	public AgendaAnnouncementAdminResDto(Long id, String title, String content, Boolean isShow,
		LocalDateTime createdAt) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.isShow = isShow;
		this.createdAt = createdAt;
	}

	@Mapper
	public interface MapStruct {

		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		AgendaAnnouncementAdminResDto toDto(AgendaAnnouncement announcement);
	}
}
