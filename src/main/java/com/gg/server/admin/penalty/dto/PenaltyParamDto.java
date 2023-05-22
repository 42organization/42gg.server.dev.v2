package com.gg.server.admin.penalty.dto;

import com.gg.server.global.dto.PageRequestDto;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PenaltyParamDto extends PageRequestDto {
    @Min(1) @Max(30)
    String intraId;
    @NotNull
    Boolean current;

    public PenaltyParamDto(Integer page, Integer size, String intraId, Boolean current) {
        super(page, size);
        this.intraId = intraId;
        this.current = current;
    }
}
