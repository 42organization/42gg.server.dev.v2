package gg.pingpong.api.admin.manage.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementAdminAddDto {
	@NotNull(message = "plz. content")
	private String content;
	@NotNull(message = "plz. creatorIntraId")
	private String creatorIntraId;
}
