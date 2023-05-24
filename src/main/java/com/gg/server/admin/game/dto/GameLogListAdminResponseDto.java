package com.gg.server.admin.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameLogListAdminResponseDto {
    private List<GameLogAdminDto> gameLogList;
    private int totalPage;
    private int currentPage;

}
