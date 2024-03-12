package gg.party.api.admin.penalty.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.penalty.PartyPenaltyAdminRepository;
import gg.auth.UserDto;
import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.party.api.admin.penalty.requset.PartyPenaltyAdminReqDto;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.PartyPenaltyNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyPenaltyAdminService {
	private final PartyPenaltyAdminRepository partyPenaltyAdminRepository;
	private final UserRepository userRepository;

	/**
	 * 패널티 수정
	 * @param penaltyId 패널티 번호
	 * @exception PartyPenaltyNotFoundException 유효하지 않은 패널티
	 */
	@Transactional
	public void modifyAdminPenalty(Long penaltyId, PartyPenaltyAdminReqDto reqDto, UserDto user) {
		User userEntity = userRepository.findById(user.getId()).get();
		PartyPenalty partyPenalty = partyPenaltyAdminRepository.findById(penaltyId)
			.orElseThrow(PartyPenaltyNotFoundException::new);
		partyPenalty.update(reqDto.getPenaltyType(), reqDto.getMessage(), reqDto.getPenaltyTime(), userEntity);
	}

	/**
	 * 패널티 부여
	 */
	@Transactional
	public void addAdminPenalty(PartyPenaltyAdminReqDto reqDto, UserDto user) {
		User userEntity = userRepository.findById(user.getId()).get();
		LocalDateTime startTime = LocalDateTime.now();
		partyPenaltyAdminRepository.save(reqDto.toEntity(userEntity, reqDto.getPenaltyType(),
			reqDto.getMessage(), startTime, reqDto.getPenaltyTime()));
	}
}
