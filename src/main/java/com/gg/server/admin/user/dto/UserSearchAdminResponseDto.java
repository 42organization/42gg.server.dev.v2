package com.gg.server.admin.user.dto;

import com.gg.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserSearchAdminResponseDto {
    private List<UserSearchAdminDto> userSearchAdminDtos;
    private Integer totalPage;

    public void filterUser(String filterString) {
        if (filterString == null)
            return ;
        this.userSearchAdminDtos = this.userSearchAdminDtos.stream()
                .filter(userSearchAdminDto -> userSearchAdminDto.getIntraId().equals(filterString))
                .collect(Collectors.toList());
    }
}
