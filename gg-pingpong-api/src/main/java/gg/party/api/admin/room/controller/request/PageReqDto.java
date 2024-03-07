package gg.party.api.admin.room.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageReqDto {
	@Min(value = 1)
	@NotNull(message = "page 는 필수 값입니다.")
	private Integer page;

	@Min(value = 1)
	private Integer size = 20;
}
