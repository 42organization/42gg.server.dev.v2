package com.gg.server.domain.game.dto.req;

import com.gg.server.global.dto.PageRequestDto;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class NormalGameListReqDto extends PageRequestDto {
    private String intraId;

    public NormalGameListReqDto(Integer page, Integer size, String intraId) {
        super(page, size);
        this.intraId = intraId;
    }
}
