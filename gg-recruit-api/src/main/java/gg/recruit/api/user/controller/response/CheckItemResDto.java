package gg.recruit.api.user.controller.response;

import gg.recruit.api.user.service.response.CheckItemSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CheckItemResDto {
	private Long id;
	private String contents;

	public CheckItemResDto(CheckItemSvcDto dto) {
		this.id = dto.getId();
		this.contents = dto.getContents();
	}
}
