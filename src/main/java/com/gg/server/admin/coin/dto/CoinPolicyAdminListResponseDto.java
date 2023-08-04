package com.gg.server.admin.coin.dto;

import com.gg.server.admin.announcement.dto.AnnouncementAdminResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CoinPolicyAdminListResponseDto {
    private List<CoinPolicyAdminResponseDto> coinPolicyList;
    private int totalPage;
}
