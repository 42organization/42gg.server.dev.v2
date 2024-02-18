package com.gg.server.admin.penalty.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gg.server.admin.penalty.data.PenaltyAdminRepository;
import com.gg.server.admin.penalty.data.PenaltyUserAdminRedisRepository;
import com.gg.server.data.manage.Penalty;
import com.gg.server.data.manage.redis.RedisPenaltyUser;
import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.penalty.exception.PenaltyExpiredException;
import com.gg.server.domain.penalty.exception.PenaltyNotFoundException;
import com.gg.server.domain.penalty.exception.RedisPenaltyUserNotFoundException;
import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.utils.ReflectionUtilsForUnitTest;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PenaltyAdminServiceTest {

	@Mock
	PenaltyUserAdminRedisRepository penaltyUserAdminRedisRepository;
	@Mock
	UserFindService userFindService;
	@Mock
	PenaltyAdminRepository penaltyRepository;
	@InjectMocks
	PenaltyAdminService penaltyAdminService;

	@Nested
	@DisplayName("givePenalty - 유저에게 penalty 부여 한다")
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
			given(userFindService.findByIntraId(userDto.getIntraId())).willReturn(user);
			doNothing().when(penaltyUserAdminRedisRepository)
				.addPenaltyUser(any(RedisPenaltyUser.class), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("Success - redis 에 PenaltyUser 가 있을 때")
		void success1() {
			RedisPenaltyUser redisPenaltyUser = new RedisPenaltyUser();
			redisPenaltyUser.updateReleaseTime(LocalDateTime.now(), penaltyTime);
			Optional<RedisPenaltyUser> optional = Optional.of(redisPenaltyUser);
			Penalty penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", redisPenaltyUser.getReleaseTime(),
				penaltyTime);

			given(penaltyUserAdminRedisRepository.findByIntraId(userDto.getIntraId())).willReturn(
				optional); // redis 에 PenaltyUser 가 있을때
			given(penaltyRepository.save(any(Penalty.class))).willReturn(penalty);

			// when, then
			penaltyAdminService.givePenalty(user.getIntraId(), penaltyTime, "reason");
		}

		@Test
		@DisplayName("Success - redis 에 PenaltyUser 가 없을 때")
		void success2() {
			// given
			Penalty penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", LocalDateTime.now(), penaltyTime);

			given(penaltyUserAdminRedisRepository.findByIntraId(user.getIntraId())).willReturn(
				Optional.empty()); // redis 에 PenaltyUser 가 없을때
			given(penaltyRepository.save(any(Penalty.class))).willReturn(penalty);

			// when, then
			penaltyAdminService.givePenalty(user.getIntraId(), penaltyTime, "reason");
		}
	}

	@Nested
	@DisplayName("getAllPenalties - 모든 penalty 를 가져온다")
	class GetAllPenaltiesTest {

		@Test
		@DisplayName("Success - 현재 penalty 를 가져온다, Boolean current = true")
		void success1() {
			//given
			given(penaltyRepository.findAllCurrent(any(Pageable.class), any(LocalDateTime.class))).willReturn(
				Page.empty());
			//when, then
			penaltyAdminService.getAllPenalties(Pageable.unpaged(), true);
		}

		@Test
		@DisplayName("Success - 모든 penalty 를 가져온다, Boolean current = false")
		void success2() {
			//given
			given(penaltyRepository.findAll(any(Pageable.class))).willReturn(Page.empty());
			//when, then
			penaltyAdminService.getAllPenalties(Pageable.unpaged(), false);
		}
	}

	@Nested
	@DisplayName("deletePenalty 테스트 - redis에 있는 penalty 삭제, 뒤 시간 penalty 시간 당기고 penalty 삭제, ")
	class DeletePenaltyTest {
		Penalty penalty;

		@BeforeEach
		void beforeEach() {
			User user = new User("intra", "email", "image", RacketType.PENHOLDER,
				RoleType.USER, 1000, SnsType.NONE, 4242L);
			penalty = new Penalty(user, PenaltyType.NOSHOW, "noshow 패널티", LocalDateTime.now(), 10);
			ReflectionUtilsForUnitTest.setFieldWithReflection(penalty, "id", 1L);
		}

		@Test
		@DisplayName("Success - penalty 삭제")
		void success() {
			//given
			given(penaltyRepository.findById(1L)).willReturn(Optional.of(penalty));
			given(penaltyUserAdminRedisRepository.findByIntraId(anyString()))
				.willReturn(Optional.of(new RedisPenaltyUser()));
			doNothing()
				.when(penaltyUserAdminRedisRepository).deletePenaltyInUser(any(RedisPenaltyUser.class), anyInt());
			doNothing()
				.when(penaltyRepository).delete(any(Penalty.class));
			//when, then
			penaltyAdminService.deletePenalty(1L);
		}

		@Test
		@DisplayName("Fail - penalty 삭제, penalty 시간이 지났을 경우")
		void fail() {
			ReflectionUtilsForUnitTest.setFieldWithReflection(penalty, "startTime",
				LocalDateTime.now().minusMinutes(20));
			//given
			given(penaltyRepository.findById(1L)).willReturn(Optional.of(penalty));
			//when, then
			Assertions.assertThrows(PenaltyExpiredException.class, () -> {
				penaltyAdminService.deletePenalty(1L);
			});
		}

		@Test
		@DisplayName("Fail - penalty 삭제, penalty 가 없을 경우")
		void fail2() {
			//given
			given(penaltyRepository.findById(1L)).willReturn(Optional.empty());
			//when, then
			Assertions.assertThrows(PenaltyNotFoundException.class, () -> {
				penaltyAdminService.deletePenalty(1L);
			});
		}

		@Test
		@DisplayName("Fail - penalty 삭제, redis 에 penalty 가 없을 경우")
		void fail3() {
			//given
			given(penaltyRepository.findById(1L)).willReturn(Optional.of(penalty));
			given(penaltyUserAdminRedisRepository.findByIntraId(anyString()))
				.willReturn(Optional.empty());
			//when, then
			Assertions.assertThrows(RedisPenaltyUserNotFoundException.class, () -> {
				penaltyAdminService.deletePenalty(1L);
			});
		}
	}

	@Nested
	@DisplayName("getAllPenaltiesByIntraId - intraId 로 패널티 리스트 조회")
	class GetAllPenaltiesByIntraIdTest {

		@Test
		@DisplayName("Success - 현재 penalty 들을 가져온다, Boolean current = true")
		void success1() {
			//given
			given(penaltyRepository.findAllCurrentByIntraId(any(Pageable.class), any(LocalDateTime.class), anyString()))
				.willReturn(Page.empty());
			//when, then
			penaltyAdminService.getAllPenaltiesByIntraId(Pageable.unpaged(), "intra", true);
		}

		@Test
		@DisplayName("Success - 모든 penalty 를 가져 온다, Boolean current = false")
		void success2() {
			//given
			given(penaltyRepository.findAllByIntraId(any(Pageable.class), anyString()))
				.willReturn(Page.empty());
			//when, then
			penaltyAdminService.getAllPenaltiesByIntraId(Pageable.unpaged(), "intra", false);
		}
	}
}
