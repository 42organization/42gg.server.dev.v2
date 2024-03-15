package gg.recruit.api.user.service.response;

import lombok.Getter;

@Getter
public class CheckItemSvcDto {
	private Long id;
	private String contents;

	public CheckItemSvcDto(Long id, String contents) {
		this.id = id;
		this.contents = contents;
	}
}
