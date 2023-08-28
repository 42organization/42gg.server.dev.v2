package com.gg.server.domain.item.dto;

import com.gg.server.global.dto.PageRequestDto;
import lombok.Getter;

@Getter
public class UserItemPageRequestDto extends PageRequestDto {

    public UserItemPageRequestDto(Integer page, Integer size) {
        super(page, size);
    }
}
