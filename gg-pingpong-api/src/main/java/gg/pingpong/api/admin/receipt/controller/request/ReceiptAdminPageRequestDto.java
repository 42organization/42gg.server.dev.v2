package gg.pingpong.api.admin.receipt.controller.request;

import gg.pingpong.api.global.dto.PageRequestDto;
import lombok.Getter;

@Getter
public class ReceiptAdminPageRequestDto extends PageRequestDto {
	private String intraId;

	public ReceiptAdminPageRequestDto(String intraId, Integer page, Integer size) {
		super(page, size);
		this.intraId = intraId;
	}
}
