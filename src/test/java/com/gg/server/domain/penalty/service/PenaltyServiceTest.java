package com.gg.server.domain.penalty.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.data.manage.Penalty;
import com.gg.server.data.manage.redis.RedisPenaltyUser;
import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.penalty.data.PenaltyRepository;
import com.gg.server.domain.penalty.redis.PenaltyUserRedisRepository;
import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PenaltyServiceTest {
	@Mock
	PenaltyRepository penaltyRepository;
	@Mock
	PenaltyUserRedisRepository penaltyUserRedisRepository;
	@Mock
	UserFindService userFindService;
	@InjectMocks
	PenaltyService penaltyService;

	@Nested
	@DisplayName("givePenalty - User에게 penalty 부여 한다")
	class GivePenaltyTest {

		User user;
		UserDto userDto;
		int penaltyTime;

		@BeforeEach
		void beforeEach() {
			user = new User("intra", "email", "image", RacketType.PENHOLDER,
				RoleType.USER, 1000, SnsType.NONE, 4242L);
			userDto = UserDto.from(user);
			penaltyTime = 1;
			given(userFindService.findUserById(userDto.getId())).willReturn(user);
			doNothing().when(penaltyUserRedisRepository)
				.addPenaltyUser(any(RedisPenaltyUser.class), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("Success : redis 에 PenaltyUser 가 있을때")
		void success1() {
			RedisPenaltyUser redisPenaltyUser = new RedisPenaltyUser();
			redisPenaltyUser.updateReleaseTime(LocalDateTime.now(), penaltyTime);
			Optional<RedisPenaltyUser> optional = Optional.of(redisPenaltyUser);
			Penalty penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", redisPenaltyUser.getReleaseTime(),
				penaltyTime);

			given(penaltyUserRedisRepository.findByIntraId(userDto.getIntraId())).willReturn(
				optional); // redis 에 PenaltyUser 가 있을때
			given(penaltyRepository.save(any(Penalty.class))).willReturn(penalty);

			// when, then
			penaltyService.givePenalty(userDto, penaltyTime);
		}

		@Test
		@DisplayName("Success : redis 에 PenaltyUser 가 없을때")
		void success2() {
			// given
			Penalty penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", LocalDateTime.now(), penaltyTime);

			given(penaltyUserRedisRepository.findByIntraId(userDto.getIntraId())).willReturn(
				Optional.empty()); // redis 에 PenaltyUser 가 없을때
			given(penaltyRepository.save(any(Penalty.class))).willReturn(penalty);

			// when, then
			penaltyService.givePenalty(userDto, penaltyTime);
		}
	}

	@Test
	@DisplayName("isPenaltyUser - Redis 에 PenaltyUser 가 존재 하는지 확인 한다")
	void isPenaltyUser() {
		//given
		given(penaltyUserRedisRepository.findByIntraId("intraId")).willReturn(Optional.of(new RedisPenaltyUser()));

		//when then
		penaltyService.isPenaltyUser("intraId");
	}
}
