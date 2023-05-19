package com.gg.server.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserSearchAdminResponseDto {
    private List<UserSearchAdminDto> userSearchAdminDtos;
    private Integer totalPage;
    private Integer currentPage;
}
