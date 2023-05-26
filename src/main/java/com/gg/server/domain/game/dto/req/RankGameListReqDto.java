package com.gg.server.domain.game.dto.req;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class RankGameListReqDto extends NormalGameListReqDto {
    @Positive
    @NotNull(message = "seasonId 는 필수 값입니다.")
    private Long seasonId;

    public RankGameListReqDto(Integer pageNum, Integer pageSize, String nickname, Long seasonId) {
        super(pageNum, pageSize, nickname);
        this.seasonId = seasonId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
