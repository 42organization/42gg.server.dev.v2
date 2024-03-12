package gg.party.api.admin.penalty.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.penalty.PartyPenaltyAdminRepository;
import gg.party.api.admin.penalty.controller.request.PageReqDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyAdminResDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyListAdminResDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyPenaltyAdminService {

	private final PartyPenaltyAdminRepository penaltyAdminRepository;

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

		Page<PartyPenaltyAdminResDto> penaltyPage = penaltyAdminRepository.findAll(pageable)
			.map(PartyPenaltyAdminResDto::new);

		List<PartyPenaltyAdminResDto> penaltyList = penaltyPage.getContent();

		return new PartyPenaltyListAdminResDto(penaltyList, penaltyPage.getTotalPages());
	}

}
