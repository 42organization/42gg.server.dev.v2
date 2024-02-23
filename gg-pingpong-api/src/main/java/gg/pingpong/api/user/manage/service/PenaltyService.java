package gg.pingpong.api.user.manage.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gg.pingpong.api.user.manage.redis.PenaltyUserRedisRepository;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.api.user.user.service.UserFindService;
import gg.pingpong.data.manage.Penalty;
import gg.pingpong.data.manage.redis.RedisPenaltyUser;
import gg.pingpong.data.manage.type.PenaltyType;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.penalty.PenaltyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PenaltyService {
	private final PenaltyRepository penaltyRepository;
	private final PenaltyUserRedisRepository penaltyUserRedisRepository;
	private final UserFindService userFindService;

	/**
	 * penalty 1분 부여
	 * **/
	public void givePenalty(UserDto userDto, Integer penaltyMinutes) {

		User user = userFindService.findUserById(userDto.getId());
		Optional<RedisPenaltyUser> redisPenaltyUser = penaltyUserRedisRepository
			.findByIntraId(userDto.getIntraId());
		LocalDateTime releaseTime;
		RedisPenaltyUser penaltyUser;
		Penalty penalty;
		LocalDateTime now = LocalDateTime.now();
		if (redisPenaltyUser.isPresent()) {
			releaseTime = redisPenaltyUser.get().getReleaseTime().plusMinutes(penaltyMinutes);
			penaltyUser = new RedisPenaltyUser(userDto.getIntraId(),
				redisPenaltyUser.get().getPenaltyTime() + penaltyMinutes,
				releaseTime, redisPenaltyUser.get().getStartTime(), "[AUTO] 매칭 취소");
			penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", redisPenaltyUser.get().getReleaseTime(),
				penaltyMinutes);
		} else {
			releaseTime = now.plusMinutes(penaltyMinutes);
			penaltyUser = new RedisPenaltyUser(user.getIntraId(), penaltyMinutes, releaseTime, now, "[AUTO] 매칭 취소");
			penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", now, penaltyMinutes);
		}
		penaltyRepository.save(penalty);
		penaltyUserRedisRepository.addPenaltyUser(penaltyUser, releaseTime);
	}

	public Boolean isPenaltyUser(String intraId) {
		return penaltyUserRedisRepository.findByIntraId(intraId).isPresent();
	}
}
