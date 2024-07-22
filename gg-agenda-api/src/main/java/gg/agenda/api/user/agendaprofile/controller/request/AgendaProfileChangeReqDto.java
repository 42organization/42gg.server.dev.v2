package gg.agenda.api.user.agendaprofile.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AgendaProfileChangeReqDto {

	@NotBlank
	@Size(max = 50, message = "userContent의 길이가 허용된 범위를 초과합니다.")
	private String userContent;

	@URL
	@Size(max = 100, message = "userGithub의 길이가 허용된 범위를 초과합니다.")
	private String userGithub;

	@Builder
	public AgendaProfileChangeReqDto(String userContent, String userGithub) {
		this.userContent = userContent;
		this.userGithub = userGithub;
	}
}
