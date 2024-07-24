package gg.agenda.api.admin.agendaannouncement.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAnnouncementAdminUpdateReqDto {

	@NotNull
	private Long id;

	@NotBlank
	@Length(max = 50)
	private String title;

	@NotBlank
	@Length(max = 1000)
	private String content;

	@NotNull
	private Boolean isShow;

	@Builder
	public AgendaAnnouncementAdminUpdateReqDto(Long id, String title, String content, Boolean isShow) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.isShow = isShow;
	}
}
