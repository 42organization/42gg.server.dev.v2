package com.gg.server.domain.game.dto.req;

import com.gg.server.domain.game.type.StatusType;
import lombok.Getter;

import javax.validation.constraints.Null;

@Getter
public class GameListReqDto extends NormalGameListReqDto {
    private StatusType status;
    public GameListReqDto(Integer pageNum, Integer pageSize, StatusType status) {
        super(pageNum, pageSize);
        this.status = status;
    }
}
