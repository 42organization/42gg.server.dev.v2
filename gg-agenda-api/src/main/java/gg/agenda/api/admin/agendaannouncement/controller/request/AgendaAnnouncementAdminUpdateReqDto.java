package gg.agenda.api.admin.agendaannouncement.controller.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAnnouncementAdminUpdateReqDto {

	@NotNull
	private Long id;

	private String title;

	private String content;

	private Boolean isShow;
}
