package com.gg.server.global.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PageRequestDto {

    @Min(value = 1, message = "page must be greater than 0")
    @NotNull(message = "page 는 필수 값입니다.")
    private Integer page;

    @Min(value = 1, message = "size must be greater than 0")
    @Max(value = 30, message = "size must be less than 30")
    private Integer size = 20;

    public PageRequestDto(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}
