package gg.recruit.api.user.service.response;

import gg.data.recruit.recruitment.CheckList;
import lombok.Getter;

@Getter
public class CheckItemSvcDto {
	private Long id;
	private String contents;

	public CheckItemSvcDto(CheckList checkList) {
		this.id = checkList.getId();
		this.contents = checkList.getContent();
	}
}
