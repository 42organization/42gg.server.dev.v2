package gg.party.api.user.category.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CategoryListResDto {
	private List<CategoryResDto> categoryList;

	public CategoryListResDto(List<CategoryResDto> categoryList) {
		this.categoryList = categoryList;
	}
}
