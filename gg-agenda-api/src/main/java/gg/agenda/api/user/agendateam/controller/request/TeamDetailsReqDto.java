package gg.agenda.api.user.agendateam.controller.request;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamDetailsReqDto {
	@NotNull
	private UUID teamKey;
}
