package gg.party.api.admin.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.category.service.CategoryAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/categories")
public class CategoryAdminController {
	private final CategoryAdminService categoryAdminService;

	/**
	 * 카테고리 삭제
	 * @return 삭제 성공 여부
	 */
	@DeleteMapping("{category_id}")
	public ResponseEntity<Void> categoryRemove(@PathVariable("category_id") Long categoryId) {
		categoryAdminService.removeCategory(categoryId);
		return ResponseEntity.noContent().build();
	}
}
