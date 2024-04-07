package gg.recruit.api.admin.controller.response;

import gg.recruit.api.admin.service.result.CheckItemAdminSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CheckItemAdminResDto {
	private Long id;
	private String contents;

	public CheckItemAdminResDto(CheckItemAdminSvcDto dto) {
		this.id = dto.getId();
		this.contents = dto.getContents();
	}
}
