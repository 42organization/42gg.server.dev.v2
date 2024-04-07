package gg.party.api.user.penalty.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.data.party.PartyPenalty;
import gg.party.api.user.penalty.controller.response.PenaltyResDto;
import gg.repo.party.PartyPenaltyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyPenaltyService {
	private final PartyPenaltyRepository partyPenaltyRepository;

	/**
	 * 현재 유저가 패널티 상태인지 조회
	 * @return 언제까지 패널티인지 DateTime으로 리턴
	 */
	@Transactional
	public PenaltyResDto findIsPenalty(UserDto userDto) {
		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(
			userDto.getId());
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			return new PenaltyResDto(partyPenalty.get());
		} else {
			return new PenaltyResDto();
		}
	}
}
