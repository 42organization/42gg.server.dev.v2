package gg.pingpong.api.admin.megaphone.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MegaphoneHistoryResponseDto {
	private List<MegaphoneAdminResponseDto> megaphoneList;
	private int totalPage;

	public MegaphoneHistoryResponseDto(List<MegaphoneAdminResponseDto> newDtos, int totalPage) {
		this.megaphoneList = newDtos;
		this.totalPage = totalPage;
	}
}
