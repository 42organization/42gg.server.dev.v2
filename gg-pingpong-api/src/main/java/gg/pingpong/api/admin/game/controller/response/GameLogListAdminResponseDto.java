package gg.pingpong.api.admin.game.controller.response;

import java.util.List;

import gg.pingpong.api.admin.game.dto.GameLogAdminDto;
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
