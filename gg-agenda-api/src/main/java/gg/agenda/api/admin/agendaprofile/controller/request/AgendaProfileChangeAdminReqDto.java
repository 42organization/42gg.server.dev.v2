package gg.agenda.api.admin.agendaprofile.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AgendaProfileChangeAdminReqDto {

	@NotBlank
	@Size(max = 50, message = "userContent의 길이가 허용된 범위를 초과합니다.")
	private String userContent;

	@URL
	@Size(max = 100, message = "userGithub의 길이가 허용된 범위를 초과합니다.")
	private String userGithub;

	@NotBlank
	private String userLocation;

	@Builder
	public AgendaProfileChangeAdminReqDto(String userContent, String userGithub, String userLocation) {
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userLocation = userLocation;
	}
}
