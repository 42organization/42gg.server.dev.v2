package gg.party.api.user.category.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.party.api.user.category.controller.response.CategoryListResDto;
import gg.party.api.user.category.controller.response.CategoryResDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.OnPenaltyException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final UserRepository userRepository;

	/**
	 * 카테고리 전체 조회
	 * @throws OnPenaltyException 패널티 상태의 유저 입력 - 403
	 * @return 카테고리 전체 리스트 (name 순으로 오름차순 정렬)
	 */
	@Transactional(readOnly = true)
	public CategoryListResDto findCategoryList(UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).get();
		PartyPenalty partyPenalty = partyPenaltyRepository.findByUserId(user.getId());
		if (partyPenalty != null && LocalDateTime.now().isBefore(
			partyPenalty.getStartTime().plusHours(partyPenalty.getPenaltyTime()))) {
			throw new OnPenaltyException();
		}
		return new CategoryListResDto(categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
			.map(CategoryResDto::new)
			.collect(Collectors.toList()));
	}

}
