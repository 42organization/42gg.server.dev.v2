package gg.pingpong.api.admin.penalty.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.penalty.PenaltyAdminRepository;
import gg.pingpong.admin.repo.penalty.PenaltyUserAdminRedisRepository;
import gg.pingpong.api.admin.penalty.controller.response.PenaltyListResponseDto;
import gg.pingpong.api.admin.penalty.controller.response.PenaltyUserResponseDto;
import gg.pingpong.api.user.user.service.UserFindService;
import gg.pingpong.data.manage.Penalty;
import gg.pingpong.data.manage.redis.RedisPenaltyUser;
import gg.pingpong.data.manage.type.PenaltyType;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.exception.penalty.PenaltyExpiredException;
import gg.pingpong.utils.exception.penalty.PenaltyNotFoundException;
import gg.pingpong.utils.exception.penalty.RedisPenaltyUserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PenaltyAdminService {
	private final PenaltyUserAdminRedisRepository penaltyUserAdminRedisRepository;
	private final UserFindService userFindService;
	private final PenaltyAdminRepository penaltyRepository;

	@Transactional
	public void givePenalty(String intraId, Integer penaltyTime, String reason) {
		User user = userFindService.findByIntraId(intraId);
		Optional<RedisPenaltyUser> redisPenaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
		LocalDateTime releaseTime;
		RedisPenaltyUser penaltyUser;
		Penalty penalty;
		LocalDateTime now = LocalDateTime.now();
		if (redisPenaltyUser.isPresent()) {
			releaseTime = redisPenaltyUser.get().getReleaseTime().plusHours(penaltyTime);
			penaltyUser = new RedisPenaltyUser(intraId, redisPenaltyUser.get().getPenaltyTime() + penaltyTime * 60,
				releaseTime, redisPenaltyUser.get().getStartTime(), reason);
			penalty = new Penalty(user, PenaltyType.NOSHOW, reason, redisPenaltyUser.get().getReleaseTime(),
				penaltyTime * 60);
		} else {
			releaseTime = now.plusHours(penaltyTime);
			penaltyUser = new RedisPenaltyUser(intraId, penaltyTime * 60, releaseTime, now, reason);
			penalty = new Penalty(user, PenaltyType.NOSHOW, reason, now, penaltyTime * 60);
		}
		penaltyRepository.save(penalty);
		penaltyUserAdminRedisRepository.addPenaltyUser(penaltyUser, releaseTime);
	}

	@Transactional(readOnly = true)
	public PenaltyListResponseDto getAllPenalties(Pageable pageable, Boolean current) {
		Page<Penalty> allPenalties;
		if (current) {
			allPenalties = penaltyRepository.findAllCurrent(pageable, LocalDateTime.now());
		} else {
			allPenalties = penaltyRepository.findAll(pageable);
		}
		Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
		return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
	}

	@Transactional
	public void deletePenalty(Long penaltyId) {
		Penalty penalty = penaltyRepository.findById(penaltyId).orElseThrow(()
			-> new PenaltyNotFoundException());
		if (penalty.getStartTime().plusMinutes(penalty.getPenaltyTime()).isBefore(LocalDateTime.now())) {
			throw new PenaltyExpiredException();
		}
		RedisPenaltyUser penaltyUser = penaltyUserAdminRedisRepository
			.findByIntraId(penalty.getUser().getIntraId()).orElseThrow(()
				-> new RedisPenaltyUserNotFoundException());
		penaltyUserAdminRedisRepository.deletePenaltyInUser(penaltyUser,
			penalty.getPenaltyTime()); //redis 시간 줄여주기
		//뒤에 있는 penalty 시간 당겨주기
		modifyStartTimeOfAfterPenalties(penalty);
		penaltyRepository.delete(penalty);
	}

	@Transactional(readOnly = true)
	public PenaltyListResponseDto getAllPenaltiesByIntraId(Pageable pageable, String intraId, Boolean current) {
		Page<Penalty> allPenalties;
		if (current) {
			allPenalties = penaltyRepository.findAllCurrentByIntraId(pageable, LocalDateTime.now(), intraId);
		} else {
			allPenalties = penaltyRepository.findAllByIntraId(pageable, intraId);
		}
		Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
		return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
	}

	private void modifyStartTimeOfAfterPenalties(Penalty penalty) {
		List<Penalty> afterPenalties = penaltyRepository.findAfterPenaltiesByUser(penalty.getUser().getId(),
			penalty.getStartTime());
		LocalDateTime newStartTime;
		if (penalty.getStartTime().isAfter(LocalDateTime.now())) {
			newStartTime = penalty.getStartTime();
		} else {
			newStartTime = LocalDateTime.now();
		}
		for (Penalty afterPenalty : afterPenalties) {
			afterPenalty.updateStartTime(newStartTime);
			newStartTime = newStartTime.plusMinutes(afterPenalty.getPenaltyTime());
		}
	}
}
