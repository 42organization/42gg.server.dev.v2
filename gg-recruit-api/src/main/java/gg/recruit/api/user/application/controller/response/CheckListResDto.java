package gg.recruit.api.user.application.controller.response;

import gg.recruit.api.user.application.service.response.CheckListSvcDto;
import lombok.Getter;

@Getter
public class CheckListResDto {
	private Long checkListId;
	private String content;

	public CheckListResDto(CheckListSvcDto checkListSvcDto) {
		this.checkListId = checkListSvcDto.getCheckListId();
		this.content = checkListSvcDto.getContent();
	}
}
