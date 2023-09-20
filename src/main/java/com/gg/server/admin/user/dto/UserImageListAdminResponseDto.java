package com.gg.server.admin.user.dto;

import com.gg.server.admin.coin.dto.CoinPolicyAdminResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageListAdminResponseDto {
    private List<UserImageAdminDto> userImageList;
    private int totalPage;
}
