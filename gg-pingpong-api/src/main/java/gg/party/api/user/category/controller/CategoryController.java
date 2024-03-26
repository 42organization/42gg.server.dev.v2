package gg.party.api.user.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.party.api.user.category.controller.response.CategoryListResDto;
import gg.party.api.user.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/categories")
public class CategoryController {
	private final CategoryService categoryService;

	/**
	 * 카테고리 조회
	 * @return 카테고리 전체 리스트
	 */
	@GetMapping
	public ResponseEntity<CategoryListResDto> categoryList(@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.findCategoryList(user));
	}
}
