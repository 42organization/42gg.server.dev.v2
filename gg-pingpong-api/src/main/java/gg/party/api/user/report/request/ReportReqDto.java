package gg.party.api.user.report.request;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportReqDto {
	@Size(min = 1, max = 200)
	private String content;
}
