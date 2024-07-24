package gg.agenda.api.user.agendateam.controller.request;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamUpdateReqDto {
	@NotBlank
	private UUID teamKey;
	@NotBlank
	private String teamContent;
	@NotBlank
	private String teamName;
	@NotNull
	private Boolean teamIsPrivate;
	@NotBlank
	private String teamLocation;
}
