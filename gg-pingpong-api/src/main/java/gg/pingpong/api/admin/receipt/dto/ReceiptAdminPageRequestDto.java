package gg.pingpong.api.admin.receipt.dto;

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
