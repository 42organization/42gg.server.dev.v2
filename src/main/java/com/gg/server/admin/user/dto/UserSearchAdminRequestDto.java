package com.gg.server.admin.user.dto;

import com.gg.server.global.dto.PageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class UserSearchAdminRequestDto extends PageRequestDto {
    private String intraId;
    private String userFilter;

    public UserSearchAdminRequestDto(Integer page, Integer size, String intraId, String userFilter) {
        super(page, size);
        this.intraId = intraId;
        this.userFilter = userFilter;
    }
}
