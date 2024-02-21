package gg.pingpong.api.domain.noti.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.data.noti.Noti;
import com.gg.server.data.noti.type.NotiType;
import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.custom.NotExistException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("NotiServiceUnitTest")
class NotiServiceUnitTest {
	@Mock
	NotiRepository notiRepository;
	@Mock
	UserRepository userRepository;
	@InjectMocks
	NotiService notiService;

	@Nested
	@DisplayName("findNotiByUser 메서드 테스트")
	class FindNotiByUserTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			User user = new User("username", "email@example.com", "password", RacketType.NONE, RoleType.USER,
				0, SnsType.NONE, userId);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));
			when(notiRepository.findAllByUserOrderByIdDesc(user)).thenReturn(List.of(new Noti()));
			//when
			notiService.findNotiByUser(userDto);
			//then
			verify(userRepository, times(1)).findById(userId);
			verify(notiRepository, times(1)).findAllByUserOrderByIdDesc(user);
		}

		@Test
		@DisplayName("userNotFoundException")
		void userNotFoundException() {
			//given
			UserDto userDto = mock(UserDto.class);
			when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> notiService.findNotiByUser(userDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, never()).findAllByUserOrderByIdDesc(any(User.class));

		}
	}

	@Nested
	@DisplayName("findNotiByIdAndUser 메서드 테스트")
	class FindNotiByIdAndUserTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			given(userRepository.findById(any(long.class))).willReturn(Optional.of(mock(User.class)));
			given(notiRepository.findByIdAndUser(any(Long.class), any(User.class))).willReturn(
				Optional.of(mock(Noti.class)));
			//when
			notiService.findNotiByIdAndUser(userDto, 1L);
			//then
			verify(userRepository, times(1)).findById(userId);
			verify(notiRepository, times(1)).findByIdAndUser(any(Long.class), any(User.class));
		}

		@Test
		@DisplayName("userNotFoundException")
		void userNotFoundException() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			given(userRepository.findById(any(long.class))).willReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> notiService.findNotiByIdAndUser(userDto, 1L))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, never()).findByIdAndUser(any(Long.class), any(User.class));
		}

		@Test
		@DisplayName("notExistException")
		void notExistException() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			User user = new User("username", "email@example.com", "password", RacketType.NONE, RoleType.USER,
				0, SnsType.NONE, userId);
			given(userRepository.findById(any(long.class))).willReturn(Optional.of(mock(User.class)));
			given(notiRepository.findByIdAndUser(any(Long.class), any(User.class))).willReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> notiService.findNotiByIdAndUser(userDto, 1L))
				.isInstanceOf(NotExistException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, times(1)).findByIdAndUser(any(Long.class), any(User.class));
		}
	}

	@Nested
	@DisplayName("modifyNotiCheckedByUser 메서드 테스트")
	class ModifyNotiCheckedByUserTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			User user = new User("username", "email@example.com", "password", RacketType.NONE, RoleType.USER,
				0, SnsType.NONE, userId);
			List<Noti> notis = List.of(new Noti());
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(mock(User.class)));
			given(notiRepository.findAllByUser(any(User.class))).willReturn(notis);
			//when
			notiService.modifyNotiCheckedByUser(userDto);
			//then
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, times(1)).findAllByUser(any(User.class));
		}

		@Test
		@DisplayName("userNotFoundException")
		void userNotFoundException() {
			//given
			Long userId = 1L;
			UserDto userDto = mock(UserDto.class);
			when(userDto.getId()).thenReturn(userId);
			given(userRepository.findById(any(long.class))).willReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> notiService.modifyNotiCheckedByUser(userDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, never()).findAllByUser(any(User.class));
		}
	}

	@Nested
	@DisplayName("removeNotiById 메서드 테스트")
	class RemoveNotiByIdTest {
		@Test
		@DisplayName("success")
		void success() {
			//when
			notiService.removeNotiById(1L);
			//then
			verify(notiRepository, times(1)).deleteById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("removeAllNotisByUser 메서드 테스트")
	class RemoveAllNotisByUserTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(mock(User.class)));
			//when
			notiService.removeAllNotisByUser(mock(UserDto.class));
			//then
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, times(1)).deleteAllByUser(any(User.class));
		}

		@Test
		@DisplayName("userNotFoundException")
		void userNotFoundException() {
			//given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> notiService.removeAllNotisByUser(mock(UserDto.class)))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(notiRepository, never()).deleteAllByUser(any(User.class));
		}
	}

	@Nested
	@DisplayName("createMatched 메서드 테스트")
	class CreateMatchedTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			LocalDateTime startTime = LocalDateTime.now();
			//when
			Noti result = notiService.createMatched(user, startTime);
			//then
			verify(notiRepository, times(1)).save(any(Noti.class));
			assertThat(result.getUser()).isEqualTo(user);
			assertThat(result.getType()).isEqualTo(NotiType.MATCHED);
			assertThat(result.getMessage()).isEqualTo(
				startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 성사되었습니다.");
			assertThat(result.getIsChecked()).isEqualTo(false);
		}
	}

	@Nested
	@DisplayName("createMatchCancel 메서드 테스트")
	class CreateMatchCancelTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			LocalDateTime startTime = LocalDateTime.now();
			//when
			Noti result = notiService.createMatchCancel(user, startTime);
			//then
			verify(notiRepository, times(1)).save(any(Noti.class));
			assertThat(result.getUser()).isEqualTo(user);
			assertThat(result.getType()).isEqualTo(NotiType.CANCELEDBYMAN);
			assertThat(result.getMessage()).isEqualTo(
				startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 상대에 의해 취소되었습니다.");
			assertThat(result.getIsChecked()).isEqualTo(false);
		}
	}

	@Nested
	@DisplayName("createGiftNoti 메서드 테스트")
	class CreateGiftNotiTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User ownerUser = mock(User.class);
			User payUser = mock(User.class);
			String itemName = "테스트 아이템";
			//when
			Noti result = notiService.createGiftNoti(ownerUser, payUser, itemName);
			//then
			verify(notiRepository, times(1)).save(any(Noti.class));
			assertThat(result.getType()).isEqualTo(NotiType.GIFT);
			assertThat(result.getMessage()).isEqualTo(
				"ଘ(੭ˊᵕˋ)੭* ੈ✩ " + payUser.getIntraId() + "님에게 " + itemName + " 아이템을 선물받았어요!");
			assertThat(result.getIsChecked()).isEqualTo(false);
		}
	}

	@Nested
	@DisplayName("createImminentNoti 메서드 테스트")
	class CreateImminentNotiTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			String enemyIntra = "testEnemyIntra";
			NotiType notiType = NotiType.IMMINENT;
			Integer gameOpenMinute = 15;
			given(notiRepository.save(any(Noti.class))).willReturn(mock(Noti.class));
			//when
			Noti result = notiService.createImminentNoti(mock(User.class), enemyIntra, notiType, gameOpenMinute);
			//then
			verify(notiRepository, times(1)).save(any(Noti.class));
			assertThat(result).isNotNull();
		}
	}

	// @MockitoSettings(strictness = Strictness.LENIENT)
	@Nested
	@DisplayName("getMessage 메서드 테스트")
	class GetMessageTest {
		@Test
		@DisplayName("공지사항 알림 메시지")
		void announce() {
			//given
			Noti noti = mock(Noti.class);
			when(noti.getType()).thenReturn(NotiType.ANNOUNCE);
			when(noti.getMessage()).thenReturn("testMessage");
			//when
			String message = notiService.getMessage(noti);
			//then
			assertThat(message).contains("새로운 알림이 도착했핑", "testMessage", "공지사항", "42GG와 함께하는 행복한 탁구생활",
				"$$지금 즉시 접속$$ ----> https://42gg.kr");
		}

		@Test
		@DisplayName("공지사항 외 알림 메시지")
		void nonannounce() {
			//given
			Noti noti = mock(Noti.class);
			when(noti.getType()).thenReturn(NotiType.MATCHED);
			//when
			String message = notiService.getMessage(noti);
			//then
			assertThat(message).doesNotContain("공지사항");
			assertThat(message).contains("새로운 알림이 도착했핑", "42GG와 함께하는 행복한 탁구생활",
				"$$지금 즉시 접속$$ ----> https://42gg.kr");
		}
	}
}
