package gg.pingpong.api.user.user.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserHistoryResponseDto {
	private List<UserHistoryData> historics;
}
