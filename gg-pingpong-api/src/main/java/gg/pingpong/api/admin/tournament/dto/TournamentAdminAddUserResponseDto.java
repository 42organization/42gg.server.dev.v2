package gg.pingpong.api.admin.tournament.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentAdminAddUserResponseDto {
	private Long userId;

	private String intraId;

	private Boolean isJoined;

}
