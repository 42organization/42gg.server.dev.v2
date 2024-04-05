package gg.recruit.api.admin.service.param;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CheckListContent {
	@NotNull
	@NotEmpty
	@Size(min = 1, max = 100, message = "100자 이내로 입력해주세요.")
	String content;

	public CheckListContent(String content) {
		this.content = content;
	}
}
