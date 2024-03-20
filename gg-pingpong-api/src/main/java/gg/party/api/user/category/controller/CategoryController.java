package gg.party.api.user.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.user.category.controller.response.CategoryResDto;
import gg.party.api.user.category.service.CategoryService;
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
	public ResponseEntity<List<CategoryResDto>> categoryList() {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.findCategoryList());
	}
}
