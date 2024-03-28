package gg.party.api.admin.report.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportPageReqDto {
	@Min(value = 1)
	@NotNull
	private Integer page;

	@Min(value = 1)
	@Max(value = 30)
	private Integer size = 10;

	public ReportPageReqDto(Integer page, Integer size) {
		this.page = page;
		if (size == null) {
			this.size = 10;
		} else {
			this.size = size;
		}
	}
}
