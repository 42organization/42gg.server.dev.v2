package gg.party.api.admin.penalty.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.party.api.admin.penalty.controller.request.PageReqDto;
import gg.party.api.admin.penalty.controller.request.PartyPenaltyAdminReqDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyAdminResDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyListAdminResDto;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.PartyPenaltyNotFoundException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyPenaltyAdminService {
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final UserRepository userRepository;

	/**
	 * 패널티 전체 조회
	 * @param reqDto page size
	 * @return PenaltyListAdminResDto penaltyList totalPage
	 */
	@Transactional(readOnly = true)
	public PartyPenaltyListAdminResDto findAllPenalty(PageReqDto reqDto) {
		int page = reqDto.getPage();
		int size = reqDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<PartyPenaltyAdminResDto> penaltyPage = partyPenaltyRepository.findAll(pageable)
			.map(PartyPenaltyAdminResDto::new);

		List<PartyPenaltyAdminResDto> penaltyList = penaltyPage.getContent();

		return new PartyPenaltyListAdminResDto(penaltyList, penaltyPage.getTotalPages());
	}

	/**
	 * 패널티 수정
	 * @param penaltyId 패널티 번호
	 * @throws PartyPenaltyNotFoundException 유효하지 않은 패널티
	 */
	@Transactional
	public void modifyAdminPenalty(Long penaltyId, PartyPenaltyAdminReqDto reqDto) {
		PartyPenalty partyPenalty = partyPenaltyRepository.findById(penaltyId)
			.orElseThrow(PartyPenaltyNotFoundException::new);
		partyPenalty.update(reqDto.getPenaltyType(), reqDto.getMessage(), reqDto.getPenaltyTime());
	}

	/**
	 * 패널티 부여
	 */
	@Transactional
	public void addAdminPenalty(PartyPenaltyAdminReqDto reqDto) {
		User userEntity = userRepository.findByIntraId(reqDto.getUserIntraId())
			.orElseThrow(UserNotFoundException::new);
		partyPenaltyRepository.save(reqDto.toEntity(userEntity, reqDto.getPenaltyType(),
			reqDto.getMessage(), LocalDateTime.now(), reqDto.getPenaltyTime()));
	}
}
