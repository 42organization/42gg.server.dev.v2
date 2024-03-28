package gg.party.api.user.report.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReportReqDto {
	@NotBlank(message = "message가 비어있습니다")
	@Size(min = 1, max = 200, message = "message는 최소 1에서 최대 200자입니다")
	private String content;

	public ReportReqDto(String content) {
		this.content = content;
	}
}
