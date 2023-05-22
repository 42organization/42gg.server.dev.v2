package com.gg.server.global.dto;

import lombok.Getter;

import javax.validation.constraints.Min;

@Getter
public class PageRequestDto {

    @Min(value = 1, message = "page must be greater than 0")
    private Integer page;
    @Min(value = 1, message = "size must be greater than 0")
    private Integer size;
}
