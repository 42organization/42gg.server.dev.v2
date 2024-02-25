package gg.pingpong.api.user.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.data.rank.redis.RankRedis;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.rank.RedisDataNotFoundException;
import gg.pingpong.utils.exception.user.UserNotFoundException;

@UnitTest
class UserFindServiceUnitTest {
	@Mock
	UserRepository userRepository;
	@Mock
	SeasonFindService seasonFindService;
	@Mock
	RankRedisRepository rankRedisRepository;

	@InjectMocks
	UserFindService userFindService;

	@Nested
	@DisplayName("FindUserById")
	class FindUserById {
		@Test
		@DisplayName("유저 조회 성공")
		void userExist() {
			//Arrange
			User user = Mockito.mock(User.class);
			Long id = 1L;
			Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

			//Act
			User findUser = userFindService.findUserById(1L);

			//Assert
			Assertions.assertThat(user).isEqualTo(findUser);
		}

		@Test
		@DisplayName("유저 조회 실패 후 UserNotFoundException 반환")
		void userNotFound() {
			//Arrange
			Mockito.when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act, Assert
			assertThatThrownBy(() -> userFindService.findUserById(1L))
				.isInstanceOf(UserNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("FindByIntraId")
	class FindByIntraId {
		@Test
		@DisplayName("유저 조회 성공")
		void userExist() {
			//Arrange
			User user = Mockito.mock(User.class);
			String intraId = "dummy";
			Mockito.when(userRepository.findByIntraId(intraId)).thenReturn(Optional.of(user));

			//Act
			User findUser = userFindService.findByIntraId(intraId);

			//Assert
			Assertions.assertThat(user).isEqualTo(findUser);
		}

		@Test
		@DisplayName("유저 조회 실패 후 UserNotFoundException 반환")
		void userNotFound() {
			//Arrange
			String intraId = "dummy";
			Mockito.when(userRepository.findByIntraId(any(String.class))).thenReturn(Optional.empty());

			//Act, Assert
			assertThatThrownBy(() -> userFindService.findByIntraId(intraId))
				.isInstanceOf(UserNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("GetUserStatusMessage")
	class GetUserStatusMessage {
		@Test
		@DisplayName("조회 성공시 상태 메시지 반환")
		void getStatusMessageSuccess() {
			//Arrange
			Long userId = 1L;
			User targetUser = Mockito.mock(User.class);
			Mockito.when(targetUser.getId()).thenReturn(userId);

			Long seasonID = 1L;
			Season currentSeason = Mockito.mock(Season.class);

			String statusMessage = "helloWorld";
			RankRedis rankRedis = Mockito.mock(RankRedis.class);
			Mockito.when(rankRedis.getStatusMessage()).thenReturn(statusMessage);

			Mockito.when(currentSeason.getId()).thenReturn(seasonID);
			Mockito.when(seasonFindService.findCurrentSeason(any())).thenReturn(currentSeason);
			Mockito.when(rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(seasonID), userId))
				.thenReturn(rankRedis);

			//Act
			String result = userFindService.getUserStatusMessage(targetUser);

			//Assert
			Assertions.assertThat(result).isEqualTo(statusMessage);
		}

		@Test
		@DisplayName("랭크 조회 실패시 빈 문자열 반환")
		void getStatusMessageFail() {
			//Arrange
			Long userId = 1L;
			User targetUser = Mockito.mock(User.class);
			Mockito.when(targetUser.getId()).thenReturn(userId);

			Long seasonID = 1L;
			Season currentSeason = Mockito.mock(Season.class);

			Mockito.when(currentSeason.getId()).thenReturn(seasonID);
			Mockito.when(seasonFindService.findCurrentSeason(any())).thenReturn(currentSeason);
			Mockito.when(rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(seasonID), userId))
				.thenThrow(RedisDataNotFoundException.class);

			//Act
			String result = userFindService.getUserStatusMessage(targetUser);

			//Assert
			Assertions.assertThat(result).isEqualTo("");
		}
	}
}
