package com.gg.server.admin.game.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameLogListAdminResponseDto {
	private List<GameLogAdminDto> gameLogList;
	private int totalPage;

}
