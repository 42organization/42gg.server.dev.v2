package gg.recruit.api.admin.controller.request;

import gg.data.recruit.manage.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRecruitmentResultMessagePreviewReqDto {
	private MessageType messageType;
}
