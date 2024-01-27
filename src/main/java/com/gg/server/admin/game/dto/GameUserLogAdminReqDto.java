package com.gg.server.admin.game.dto;

import javax.validation.constraints.NotNull;

import com.gg.server.global.dto.PageRequestDto;

import lombok.Getter;

@Getter
public class GameUserLogAdminReqDto extends PageRequestDto {
	@NotNull(message = "plz, intraId")
	private String intraId;

	public GameUserLogAdminReqDto(String intraId, Integer page, Integer size) {
		super(page, size);
		this.intraId = intraId;
	}
}
