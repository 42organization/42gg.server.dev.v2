package gg.pingpong.api.admin.user.dto;

import java.util.List;

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
