package gg.party.api.admin.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.category.CategoryAdminRepository;
import gg.admin.repo.room.RoomAdminRepository;
import gg.data.party.Category;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.DefaultCategoryNeedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
	private final CategoryAdminRepository categoryAdminRepository;
	private final RoomAdminRepository roomAdminRepository;

	/**
	 * 카테고리 삭제
	 * 삭제 시 기존에 room에 연결되어 있던 카테고리는 default(1) 로 변경
	 * @param categoryId 삭제할 카테고리 id
	 * @exception CategoryNotFoundException 유효하지 않은 카테고리
	 * @exception DefaultCategoryNeedException default 카테고리 존재 x 또는 default 카테고리 삭제 요청
	 */
	@Transactional
	public void removeCategory(Long categoryId) {
		Category category = categoryAdminRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		Category defaultCategory = categoryAdminRepository.findById(DefaultCategoryNeedException.DEFAULT_CATEGORY_ID)
			.orElseThrow(DefaultCategoryNeedException::new);

		if (category.equals(defaultCategory)) {
			throw new DefaultCategoryNeedException();
		}

		roomAdminRepository.findByCategory(category)
			.forEach(room -> room.updateCategory(defaultCategory));

		categoryAdminRepository.deleteById(categoryId);
	}
}
