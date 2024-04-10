package gg.party.api.admin.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.category.CategoryAdminRepository;
import gg.admin.repo.room.RoomAdminRepository;
import gg.data.party.Category;
import gg.party.api.admin.category.controller.request.CategoryAddAdminReqDto;
import gg.utils.exception.party.CategoryDuplicateException;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.DefaultCategoryNeedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
	private final CategoryAdminRepository categoryAdminRepository;
	private final RoomAdminRepository roomAdminRepository;

	/**
	 * 카테고리 추가
	 * @param reqDto 추가할 카테고리 이름
	 * @throws CategoryDuplicateException 중복된 카테고리 - 409
	 */
	@Transactional
	public void addCategory(CategoryAddAdminReqDto reqDto) {
		String categoryName = reqDto.getCategoryName();

		if (categoryAdminRepository.existsByName(categoryName)) {
			throw new CategoryDuplicateException();
		}
		categoryAdminRepository.save(new Category(categoryName));
	}

	/**
	 * 카테고리 삭제
	 * 삭제 시 기존에 room에 연결되어 있던 카테고리는 etc 로 변경
	 * @param categoryId 삭제할 카테고리 id
	 * @throws CategoryNotFoundException 유효하지 않은 카테고리 - 404
	 * @throws DefaultCategoryNeedException default 카테고리 존재 x 또는 default 카테고리 삭제 요청 - 400
	 */
	@Transactional
	public void removeCategory(Long categoryId) {
		Category category = categoryAdminRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		Category defaultCategory = categoryAdminRepository.findByName(
			DefaultCategoryNeedException.DEFAULT_CATEGORY_NAME).orElseThrow(DefaultCategoryNeedException::new);

		if (category.equals(defaultCategory)) {
			throw new DefaultCategoryNeedException();
		}

		roomAdminRepository.findByCategory(category)
			.forEach(room -> room.updateCategory(defaultCategory));

		categoryAdminRepository.deleteById(categoryId);
	}
}
