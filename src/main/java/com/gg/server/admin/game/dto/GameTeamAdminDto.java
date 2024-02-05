package com.gg.server.admin.game.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameTeamAdminDto {
	private String intraId1;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String intraId2;  //복식일 경우에만 있음
	private Long teamId;
	private Integer score;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean win;

	public GameTeamAdminDto(String intraId, Long teamId, Integer score, Boolean win) {
		this.intraId1 = intraId;
		this.teamId = teamId;
		this.score = score;
		this.win = win;
	}
}
