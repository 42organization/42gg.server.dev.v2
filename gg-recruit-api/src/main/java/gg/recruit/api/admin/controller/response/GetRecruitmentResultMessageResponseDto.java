package gg.recruit.api.admin.controller.response;

import gg.data.recruit.manage.enums.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GetRecruitmentResultMessageResponseDto {
	private long messageId;
	private MessageType messageType;
	private Boolean isUse;
	private String message;
}
