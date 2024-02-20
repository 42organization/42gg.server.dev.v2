package gg.pingpong.api.admin.tournament.dto;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentAdminAddUserRequestDto {
	@NotNull(message = "intraId가 필요합니다.")
	private String intraId;
}
