package gg.party.api.admin.penalty.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.party.api.admin.penalty.request.PartyPenaltyAdminReqDto;
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
	 * 패널티 수정
	 *
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
		User userEntity = userRepository.findById(reqDto.getReportee().getId()).orElseThrow(UserNotFoundException::new);
		LocalDateTime startTime = LocalDateTime.now();
		partyPenaltyRepository.save(reqDto.toEntity(userEntity, reqDto.getPenaltyType(),
			reqDto.getMessage(), startTime, reqDto.getPenaltyTime()));
	}
}
