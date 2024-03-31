package gg.recruit.api.admin.controller.request;

import javax.validation.constraints.NotNull;

import gg.data.recruit.manage.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRecruitmentResultMessagePreviewReqDto {
	@NotNull(message = "메시지 타입을 입력해주세요.")
	private MessageType messageType;
}
