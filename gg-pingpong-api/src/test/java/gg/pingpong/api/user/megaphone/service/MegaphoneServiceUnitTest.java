package gg.pingpong.api.user.megaphone.service;

import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.user.item.service.ItemService;
import gg.pingpong.api.user.megaphone.dto.MegaphoneUseRequestDto;
import gg.pingpong.api.user.megaphone.redis.MegaphoneRedisRepository;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.store.Item;
import gg.pingpong.data.store.Megaphone;
import gg.pingpong.data.store.Receipt;
import gg.pingpong.data.store.type.ItemStatus;
import gg.pingpong.data.store.type.ItemType;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.data.user.type.SnsType;
import gg.pingpong.repo.megaphone.MegaphoneRepository;
import gg.pingpong.repo.receipt.ReceiptRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.megaphone.MegaphoneContentException;
import gg.pingpong.utils.exception.megaphone.MegaphoneNotFoundException;
import gg.pingpong.utils.exception.megaphone.MegaphoneTimeException;
import gg.pingpong.utils.exception.receipt.ItemStatusException;
import gg.pingpong.utils.exception.receipt.ReceiptNotFoundException;
import gg.pingpong.utils.exception.user.UserNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
class MegaphoneServiceUnitTest {
	@Mock
	UserRepository userRepository;
	@Mock
	ReceiptRepository receiptRepository;
	@Mock
	MegaphoneRepository megaphoneRepository;
	@Mock
	MegaphoneRedisRepository megaphoneRedisRepository;
	@Mock
	ItemService itemService;
	@InjectMocks
	MegaphoneService megaphoneService;

	User user;
	Receipt receipt;
	Item item;

	@BeforeEach
	void beforeEach() {
		user = new User("", "", "", RacketType.NONE, RoleType.USER,
			0, SnsType.NONE, 1L);
		setFieldWithReflection(user, "id", 1L);
		item = new Item();
		receipt = new Receipt(item, "", "testUser", ItemStatus.BEFORE, LocalDateTime.now());
	}

	@Nested
	@DisplayName("useMegaphone 메서드 유닛 테스트")
	class UseMegaphoneUnitTest {
		MegaphoneUseRequestDto megaphoneUseRequestDto;

		@BeforeEach
		void beforeEach() {
			setFieldWithReflection(item, "type", ItemType.MEGAPHONE);
			megaphoneUseRequestDto = new MegaphoneUseRequestDto(1L, "test");
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
			given(megaphoneRepository.save(any(Megaphone.class))).willReturn(mock(Megaphone.class));
			// when
			megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
				LocalTime.now().withHour(23).withMinute(54));
			setFieldWithReflection(receipt, "status", ItemStatus.BEFORE);
			megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
				LocalTime.now().withHour(0).withMinute(6));
			// then
			assertThat(receipt.getStatus()).isEqualTo(ItemStatus.WAITING);
			verify(userRepository, times(2)).findById(any(Long.class));
			verify(receiptRepository, times(2)).findById(any(Long.class));
			verify(itemService, times(2)).checkItemType(any(), any());
			verify(itemService, times(2)).checkItemOwner(any(), any());
			verify(megaphoneRepository, times(2)).save(any(Megaphone.class));
		}

		@Test
		@DisplayName("MegaphoneContentException")
		void megaphoneContentException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
			setFieldWithReflection(megaphoneUseRequestDto, "content", "");
			// when, then
			Assertions.assertThatThrownBy(() -> megaphoneService.useMegaphone(
					megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(54)))
				.isInstanceOf(MegaphoneContentException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
			verify(itemService, times(1)).checkItemType(any(), any());
			verify(itemService, times(1)).checkItemOwner(any(), any());
		}

		@Test
		@DisplayName("ItemStatusException")
		void itemStatusException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
			setFieldWithReflection(receipt, "status", ItemStatus.USING);
			// when, then
			Assertions.assertThatThrownBy(
				() -> megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
					LocalTime.now().withHour(23).withMinute(54))).isInstanceOf(ItemStatusException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
			verify(itemService, times(1)).checkItemType(any(), any());
			verify(itemService, times(1)).checkItemOwner(any(), any());
		}

		@Test
		@DisplayName("ReceiptNotFoundException")
		void receiptNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			Assertions.assertThatThrownBy(
				() -> megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
					LocalTime.now().withHour(23).withMinute(54))).isInstanceOf(ReceiptNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("MegaphoneTimeException")
		void megaphoneTimeException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			// when, then
			Assertions.assertThatThrownBy(
				() -> megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
					LocalTime.now().withHour(23).withMinute(56))).isInstanceOf(MegaphoneTimeException.class);
			Assertions.assertThatThrownBy(
				() -> megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
					LocalTime.now().withHour(0).withMinute(3))).isInstanceOf(MegaphoneTimeException.class);
			verify(userRepository, times(2)).findById(any(Long.class));
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			Assertions.assertThatThrownBy(
				() -> megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user),
					LocalTime.now().withHour(23).withMinute(56))).isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("setMegaphoneList 메서드 유닛 테스트")
	class SetMegaphoneListUnitTest {
		List<Megaphone> usingList = new ArrayList<>();
		List<Megaphone> waitList = new ArrayList<>();

		@BeforeEach
		void beforeEach() {
			waitList.add(new Megaphone(user, receipt, "test", LocalDate.now().plusDays(1)));
			Receipt receiptUsing = new Receipt(item, "", "testUser",
				ItemStatus.USING, LocalDateTime.now());
			usingList.add(new Megaphone(user, receiptUsing, "test", LocalDate.now()));
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			given(megaphoneRepository.findAllByUsedAtAndReceiptStatus(LocalDate.now(), ItemStatus.USING))
				.willReturn(usingList);
			given(megaphoneRepository.findAllByUsedAtAndReceiptStatus(
				LocalDate.now().plusDays(1), ItemStatus.WAITING))
				.willReturn(waitList);
			// when, then
			megaphoneService.setMegaphoneList(LocalDate.now());
			assertThat(usingList.get(0).getReceipt().getStatus()).isEqualTo(ItemStatus.USED);
			assertThat(waitList.get(0).getReceipt().getStatus()).isEqualTo(ItemStatus.USING);
			verify(megaphoneRepository, times(1))
				.findAllByUsedAtAndReceiptStatus(LocalDate.now(), ItemStatus.USING);
			verify(megaphoneRedisRepository, times(1)).deleteAllMegaphone();
			verify(megaphoneRepository, times(1))
				.findAllByUsedAtAndReceiptStatus(LocalDate.now().plusDays(1), ItemStatus.WAITING);
			verify(megaphoneRedisRepository, times(waitList.size())).addMegaphone(any());
		}
	}

	@Nested
	@DisplayName("deleteMegaphone 메서드 유닛 테스트")
	class DeleteMegaphoneUnitTest {
		Megaphone megaphone;

		@BeforeEach
		void beforeEach() {
			megaphone = new Megaphone(user, receipt, "test", LocalDate.now());
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(megaphoneRepository.findById(any(Long.class))).willReturn(Optional.of(megaphone));
			// when deleted by user
			megaphoneService.deleteMegaphone(1L, UserDto.from(user));
			// when deleted by admin, using megaphone
			setFieldWithReflection(user, "roleType", RoleType.ADMIN);
			megaphoneService.deleteMegaphone(1L, UserDto.from(user));
			// when deleting using megaphone
			setFieldWithReflection(receipt, "status", ItemStatus.USING);
			megaphoneService.deleteMegaphone(1L, UserDto.from(user));
			// then
			assertThat(receipt.getStatus()).isEqualTo(ItemStatus.DELETED);
			verify(userRepository, times(3)).findById(any(Long.class));
			verify(megaphoneRepository, times(3)).findById(any(Long.class));
			verify(itemService, times(1)).checkItemOwner(any(), any());
			verify(itemService, times(3)).checkItemStatus(any());
			verify(megaphoneRedisRepository, times(1)).deleteMegaphoneById(any());
		}

		@Test
		@DisplayName("MegaphoneNotFoundException")
		void megaphoneNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(megaphoneRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> megaphoneService.deleteMegaphone(1L, UserDto.from(user)))
				.isInstanceOf(MegaphoneNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(megaphoneRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> megaphoneService.deleteMegaphone(1L, UserDto.from(user)))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("getMegaphoneDetail 메서드 유닛 테스트")
	class GetMegaphoneDetailUnitTest {
		Megaphone megaphone;

		@BeforeEach
		void beforeEach() {
			megaphone = new Megaphone(user, receipt, "test", LocalDate.now());
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
			given(megaphoneRepository.findByReceipt(any(Receipt.class))).willReturn(Optional.of(megaphone));
			// when, then
			megaphoneService.getMegaphoneDetail(1L, UserDto.from(user));
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
			verify(itemService, times(1)).checkItemType(any(), any());
			verify(itemService, times(1)).checkItemOwner(any(), any());
			verify(itemService, times(1)).checkItemStatus(any());
			verify(megaphoneRepository, times(1)).findByReceipt(any(Receipt.class));

		}

		@Test
		@DisplayName("MegaphoneNotFoundException")
		void megaphoneNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
			given(megaphoneRepository.findByReceipt(any(Receipt.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
				.isInstanceOf(MegaphoneNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
			verify(itemService, times(1)).checkItemType(any(), any());
			verify(itemService, times(1)).checkItemOwner(any(), any());
			verify(itemService, times(1)).checkItemStatus(any());
			verify(megaphoneRepository, times(1)).findByReceipt(any(Receipt.class));
		}

		@Test
		@DisplayName("ReceiptNotFoundException")
		void receiptNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
			given(receiptRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
				.isInstanceOf(ReceiptNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(receiptRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			// given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("getMegaphoneTodayList 메서드 유닛 테스트")
	class GetMegaphoneTodayListUnitTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(megaphoneRedisRepository.getAllMegaphone()).willReturn(new ArrayList<>());
			// when, then
			megaphoneService.getMegaphoneTodayList();
			verify(megaphoneRedisRepository, times(1)).getAllMegaphone();
		}
	}
}
