package com.gg.server.admin.game.dto;

import com.gg.server.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameLogAdminRequestDto extends PageRequestDto {
    private Long seasonId;

    public GameLogAdminRequestDto(Integer page, Integer size, Long seasonId) {
        super(page, size);
        this.seasonId = seasonId;
    }
}
