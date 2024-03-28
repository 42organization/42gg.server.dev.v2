package gg.party.api.admin.category.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CategoryAddAdminReqDto {
	@NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
	@Size(min = 1, max = 10, message = "카테고리 이름은 1자 이상 10자 이하이어야 합니다.")
	private String categoryName;

	public CategoryAddAdminReqDto(String category) {
		this.categoryName = category;
	}
}
