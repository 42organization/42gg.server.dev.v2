package gg.party.api.user.category.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.party.api.user.category.controller.response.CategoryResDto;
import gg.repo.party.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 전체 조회
	 * @return 카테고리 전체 리스트 (id 순으로 오름차순 정렬)
	 */
	@Transactional(readOnly = true)
	public List<CategoryResDto> findCategoryList() {
		return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
			.map(CategoryResDto::new)
			.toList();
	}

}
