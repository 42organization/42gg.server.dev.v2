package gg.party.api.admin.penalty.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class PageReqDto {
	@NotNull
	@Min(value = 1)
	private final Integer page;

	@Min(value = 1)
	@Max(value = 30)
	private final Integer size;

	public PageReqDto(Integer page, Integer size) {
		this.page = page;
		if (size == null) {
			this.size = 10;
		} else {
			this.size = size;
		}
	}
}
