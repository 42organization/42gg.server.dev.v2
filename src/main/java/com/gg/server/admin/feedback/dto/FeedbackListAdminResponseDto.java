package com.gg.server.admin.feedback.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FeedbackListAdminResponseDto {
	private List<FeedbackAdminResponseDto> feedbackList;
	private int totalPage;

	public FeedbackListAdminResponseDto(List<FeedbackAdminResponseDto> newDtos, int totalPage) {
		this.feedbackList = newDtos;
		this.totalPage = totalPage;
	}
}
