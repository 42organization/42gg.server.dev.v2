package gg.recruit.api.user.service.response;

import gg.data.recruit.application.CheckListEntityDto;
import lombok.Getter;

@Getter
public class CheckListSvcDto {
	private Long checkListId;
	private String content;

	public CheckListSvcDto(CheckListEntityDto entityDto) {
		this.checkListId = entityDto.getCheckListId();
		this.content = entityDto.getContent();
	}
}
