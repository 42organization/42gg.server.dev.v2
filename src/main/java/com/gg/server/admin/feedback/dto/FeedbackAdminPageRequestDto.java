package com.gg.server.admin.feedback.dto;

import com.gg.server.global.dto.PageRequestDto;

import lombok.Getter;

@Getter
public class FeedbackAdminPageRequestDto extends PageRequestDto {
	String intraId;

	public FeedbackAdminPageRequestDto(String intraId, Integer page, Integer size) {
		super(page, size);
		this.intraId = intraId;
	}
}
