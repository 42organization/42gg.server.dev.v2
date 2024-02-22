package gg.pingpong.api.user.user.controller.response;

import java.util.List;

import gg.pingpong.api.user.user.dto.UserImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageResponseDto {

	List<UserImageDto> userImages;
}
