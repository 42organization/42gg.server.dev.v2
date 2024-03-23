package gg.party.api.admin.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.category.controller.request.CategoryAddAdminReqDto;
import gg.party.api.admin.category.service.CategoryAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/categories")
public class CategoryAdminController {
	private final CategoryAdminService categoryAdminService;

	/**
	 * 카테고리 추가
	 * @param reqDto 추가할 카테고리 이름
	 * @return 추가 성공 여부
	 */
	@PostMapping
	public ResponseEntity<Void> categoryAdd(@RequestBody CategoryAddAdminReqDto reqDto) {
		categoryAdminService.addCategory(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 삭제
	 * @return 삭제 성공 여부
	 */
	@DeleteMapping("{category_id}")
	public ResponseEntity<Void> categoryRemove(@PathVariable("category_id") Long categoryId) {
		categoryAdminService.removeCategory(categoryId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
