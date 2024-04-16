package gg.recruit.api.admin.controller.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GetRecruitmentResultMessagesResponseDto {
	private List<GetRecruitmentResultMessageResponseDto> messages;
}
