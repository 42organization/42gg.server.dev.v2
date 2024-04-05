package gg.recruit.api.admin.service.dto;

import lombok.Getter;

@Getter
public class RecruitmentResultMessagePreviewResultSvcDto {
	private String content;

	public RecruitmentResultMessagePreviewResultSvcDto(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
