package gg.party.api.user.category.controller.response;

import java.util.List;

import lombok.Getter;

@Getter
public class CategoryListResDto {
	private List<CategoryResDto> categoryList;

	public CategoryListResDto(List<CategoryResDto> categoryList) {
		this.categoryList = categoryList;
	}

}
