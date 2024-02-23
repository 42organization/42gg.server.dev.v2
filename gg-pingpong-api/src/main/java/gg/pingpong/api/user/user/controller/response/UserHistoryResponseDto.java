package gg.pingpong.api.user.user.controller.response;

import java.util.List;

import gg.pingpong.api.user.user.dto.UserHistoryData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserHistoryResponseDto {
	private List<UserHistoryData> historics;
}
