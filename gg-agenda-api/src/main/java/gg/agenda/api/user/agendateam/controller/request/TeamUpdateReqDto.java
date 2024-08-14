package gg.agenda.api.user.agendateam.controller.request;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamUpdateReqDto {
	@NotNull
	private UUID teamKey;
	@NotBlank
	@Size(max = 30)
	private String teamName;
	@NotBlank
	@Size(max = 500)
	private String teamContent;
	@NotNull
	private Boolean teamIsPrivate;
	@NotBlank
	@Size(max = 10)
	private String teamLocation;

	@Builder
	public TeamUpdateReqDto(UUID teamKey, String teamName, String teamContent, Boolean teamIsPrivate,
		String teamLocation) {
		this.teamKey = teamKey;
		this.teamName = teamName;
		this.teamContent = teamContent;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLocation = teamLocation;
	}
}
