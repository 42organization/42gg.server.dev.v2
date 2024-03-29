package gg.party.api.user.category.controller.response;

import gg.data.party.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CategoryResDto {
	private Long categoryId;
	private String categoryName;

	public CategoryResDto(Category category) {
		this.categoryId = category.getId();
		this.categoryName = category.getName();
	}
}
