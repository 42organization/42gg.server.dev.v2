package gg.pingpong.api.admin.user.controller.response;

import java.util.List;

import gg.pingpong.api.admin.user.dto.UserImageAdminDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageListAdminResponseDto {
	private List<UserImageAdminDto> userImageList;
	private int totalPage;
}
