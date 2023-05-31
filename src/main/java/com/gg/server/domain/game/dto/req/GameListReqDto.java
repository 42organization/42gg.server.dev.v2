package com.gg.server.domain.game.dto.req;

import com.gg.server.domain.game.type.StatusType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
public class GameListReqDto extends NormalGameListReqDto {
    private StatusType status;
    public GameListReqDto(Integer pageNum, Integer pageSize, String nickname, StatusType status) {
        super(pageNum, pageSize, nickname);
        this.status = status;
    }
}
