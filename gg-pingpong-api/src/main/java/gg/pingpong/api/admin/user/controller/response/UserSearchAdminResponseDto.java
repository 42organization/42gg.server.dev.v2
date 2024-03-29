package gg.pingpong.api.admin.user.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.pingpong.api.admin.user.dto.UserSearchAdminDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSearchAdminResponseDto {
	private List<UserSearchAdminDto> userSearchAdminDtos;
	private Integer totalPage;

	public void filterUser(String filterString) {
		if (filterString == null) {
			return;
		}
		this.userSearchAdminDtos = this.userSearchAdminDtos.stream()
			.filter(userSearchAdminDto -> userSearchAdminDto.getIntraId().equals(filterString))
			.collect(Collectors.toList());
	}
}
