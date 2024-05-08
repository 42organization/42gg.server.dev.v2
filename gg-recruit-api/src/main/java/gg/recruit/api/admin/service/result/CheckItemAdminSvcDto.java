package gg.recruit.api.admin.service.result;

import gg.data.recruit.recruitment.CheckList;
import lombok.Getter;

@Getter
public class CheckItemAdminSvcDto {
	private Long id;
	private String contents;

	public CheckItemAdminSvcDto(CheckList checkList) {
		this.id = checkList.getId();
		this.contents = checkList.getContent();
	}
}
