package com.gg.server.admin.megaphone.dto;

import com.gg.server.domain.megaphone.data.Megaphone;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MegaphoneAdminResponseDto {
	private Long megaphoneId;
	private String content;
	private String usedAt;
	private String status;
	private String intraId;

	public MegaphoneAdminResponseDto(Megaphone megaphone) {
		this.megaphoneId = megaphone.getId();
		this.content = megaphone.getContent();
		this.usedAt = megaphone.getUsedAt().toString();
		this.status = megaphone.getReceipt().getStatus().getDescription();
		this.intraId = megaphone.getUser().getIntraId();
	}
}
