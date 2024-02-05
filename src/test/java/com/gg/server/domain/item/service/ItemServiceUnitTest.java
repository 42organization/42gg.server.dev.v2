package com.gg.server.domain.item.service;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gg.server.data.store.Item;
import com.gg.server.data.store.Receipt;
import com.gg.server.data.store.type.ItemStatus;
import com.gg.server.data.store.type.ItemType;
import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.coin.service.UserCoinChangeService;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.data.UserItemRepository;
import com.gg.server.domain.item.exception.ItemNotFoundException;
import com.gg.server.domain.item.exception.ItemNotPurchasableException;
import com.gg.server.domain.item.exception.ItemTypeException;
import com.gg.server.domain.item.exception.KakaoGiftException;
import com.gg.server.domain.item.exception.KakaoPurchaseException;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ItemStatusException;
import com.gg.server.domain.receipt.exception.ReceiptNotOwnerException;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
	@Mock
	ItemRepository itemRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ReceiptRepository receiptRepository;
	@Mock
	NotiService notiService;
	@Mock// 내부 로직에서 void 값 반환으로 사용하는 곳 있음
	UserCoinChangeService userCoinChangeService;
	@Mock
	UserItemRepository userItemRepository;
	@InjectMocks
	ItemService itemService;

	@Nested
	@DisplayName("getAllItems method unitTest")
	class GetAllItemsTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			List<Item> items = new ArrayList<>();
			given(itemRepository.findAllByCreatedAtDesc()).willReturn(items);
			// when, then
			itemService.getAllItems();
			verify(itemRepository, times(1)).findAllByCreatedAtDesc();
		}
	}

	@Nested
	@DisplayName("purchaseItem method unitTest")
	class PurchaseItemTest {
		User payUser;
		UserDto userDto;
		Item item;

		@BeforeEach
		void beforeEach() {
			payUser = new User("", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
			setFieldWithReflection(payUser, "id", 1L);
			userDto = UserDto.from(payUser);
			item = new Item();
			setFieldWithReflection(item, "isVisible", true);
		}

		@Test
		@DisplayName("success -> no discount")
		void successNoDiscount() {
			// given
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(receiptRepository.save(any(Receipt.class))).willReturn(mock(Receipt.class));
			// when, then 1
			itemService.purchaseItem(1L, userDto);
			// when, then 2
			setFieldWithReflection(item, "discount", 0);
			itemService.purchaseItem(1L, userDto);
			verify(itemRepository, times(2)).findById(any(Long.class));
			verify(userRepository, times(2)).findById(any(Long.class));
			verify(userCoinChangeService, times(2)).purchaseItemCoin(any(), any(), any());
			verify(receiptRepository, times(2)).save(any(Receipt.class));

		}

		@Test
		@DisplayName("success -> discount")
		void successDiscount() {
			// given
			setFieldWithReflection(item, "price", 100);
			setFieldWithReflection(item, "discount", 10);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(receiptRepository.save(any(Receipt.class))).willReturn(mock(Receipt.class));
			// when, then
			itemService.purchaseItem(1L, userDto);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(userCoinChangeService, times(1)).purchaseItemCoin(any(), any(), any());
			verify(receiptRepository, times(1)).save(any(Receipt.class));
		}

		@Test
		@DisplayName("item not found")
		void itemNotFound() {
			// given
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemService.purchaseItem(1L, userDto))
				.isInstanceOf(ItemNotFoundException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("Item Not Purchasable")
		void itemNotPurchasable() {
			// given
			setFieldWithReflection(item, "isVisible", false);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			// when, then
			assertThatThrownBy(() -> itemService.purchaseItem(1L, userDto))
				.isInstanceOf(ItemNotPurchasableException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("User Not Found")
		void userNotFound() {
			// given
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemService.purchaseItem(1L, userDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("GuestUser Exception")
		void guestUserException() {
			// given
			setFieldWithReflection(payUser, "roleType", RoleType.GUEST);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			// when, then
			assertThatThrownBy(() -> itemService.purchaseItem(1L, userDto))
				.isInstanceOf(KakaoPurchaseException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
		}

	}

	@Nested
	@DisplayName("giftItemTest method unitTest")
	class GiftItemTest {
		User payUser;
		User owner;
		UserDto userDto;
		Item item;

		@BeforeEach
		void beforeEach() {
			payUser = new User("", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
			setFieldWithReflection(payUser, "id", 1L);
			userDto = UserDto.from(payUser);
			owner = new User("", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
			item = new Item();
			setFieldWithReflection(item, "isVisible", true);
		}

		@Test
		@DisplayName("success -> No Discount")
		void successNoDiscount() {
			// given
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(receiptRepository.save(any(Receipt.class))).willReturn(mock(Receipt.class));
			given(userRepository.findByIntraId(any(String.class))).willReturn(Optional.of(owner));
			// when, then 1
			itemService.giftItem(1L, "owner", userDto);
			// when, then 2
			setFieldWithReflection(item, "discount", 0);
			itemService.giftItem(1L, "owner", userDto);
			verify(itemRepository, times(2)).findById(any(Long.class));
			verify(userRepository, times(2)).findById(any(Long.class));
			verify(userRepository, times(2)).findByIntraId(any(String.class));
			verify(userCoinChangeService, times(2)).giftItemCoin(any(), any(), any(), any());
			verify(receiptRepository, times(2)).save(any(Receipt.class));
			verify(notiService, times(2)).createGiftNoti(any(), any(), any());

		}

		@Test
		@DisplayName("success -> Discount")
		void successDiscount() {
			// given
			setFieldWithReflection(item, "price", 100);
			setFieldWithReflection(item, "discount", 10);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(receiptRepository.save(any(Receipt.class))).willReturn(mock(Receipt.class));
			given(userRepository.findByIntraId(any(String.class))).willReturn(Optional.of(owner));
			// when, then
			itemService.giftItem(1L, "owner", userDto);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findByIntraId(any(String.class));
			verify(userCoinChangeService, times(1)).giftItemCoin(any(), any(), any(), any());
			verify(receiptRepository, times(1)).save(any(Receipt.class));
			verify(notiService, times(1)).createGiftNoti(any(), any(), any());
		}

		@Test
		@DisplayName("guest Owner")
		void guestOwnerTest() {
			// given
			setFieldWithReflection(owner, "roleType", RoleType.GUEST);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(userRepository.findByIntraId(any(String.class))).willReturn(Optional.of(owner));
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(KakaoGiftException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findByIntraId(any(String.class));
		}

		@Test
		@DisplayName("Owner not Found")
		void ownerNotFoundTest() {
			// given
			setFieldWithReflection(owner, "roleType", RoleType.GUEST);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			given(userRepository.findByIntraId(any(String.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findByIntraId(any(String.class));
		}

		@Test
		@DisplayName("guest payUser")
		void guestPayUserTest() {
			// given
			setFieldWithReflection(payUser, "roleType", RoleType.GUEST);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(payUser));
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(KakaoPurchaseException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("payUser Not Found")
		void payUserNotFoundTest() {
			// given
			setFieldWithReflection(payUser, "roleType", RoleType.GUEST);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
			verify(userRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("Item Not Purchasable")
		void itemNotPurchasableTest() {
			// given
			setFieldWithReflection(item, "isVisible", false);
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(ItemNotPurchasableException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("Item Not Found")
		void itemNotFoundTest() {
			// given
			given(itemRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemService.giftItem(1L, "owner", userDto))
				.isInstanceOf(ItemNotFoundException.class);
			verify(itemRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("getItemByUser method unitTest")
	class GetItemByUserTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			User user = new User("intraId", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
			given(userItemRepository.findByOwnerIntraId(any(String.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(new ArrayList<>()));
			// when, then
			itemService.getItemByUser(UserDto.from(user), mock(Pageable.class));
			verify(userItemRepository, times(1)).findByOwnerIntraId(any(String.class), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("checkItemOwner method unitTest")
	class CheckItemOwnerTest {
		User user;
		Receipt receipt;

		@BeforeEach
		void beforeEach() {
			user = new User("intraId", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
			receipt = new Receipt();
			setFieldWithReflection(receipt, "ownerIntraId", user.getIntraId());
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			// when, then
			itemService.checkItemOwner(user, receipt);
		}

		@Test
		@DisplayName("Receipt Not Owner Exception")
		void receiptNotOwnerTest() {
			// given
			setFieldWithReflection(receipt, "ownerIntraId", user.getIntraId() + "1234");
			// when, then
			assertThatThrownBy(() -> itemService.checkItemOwner(user, receipt))
				.isInstanceOf(ReceiptNotOwnerException.class);
		}
	}

	@Nested
	@DisplayName("checkItemType method unitTest")
	class CheckItemTypeTest {
		Receipt receipt;

		@BeforeEach
		void beforeEach() {
			receipt = new Receipt();
			Item item = new Item();
			setFieldWithReflection(item, "type", ItemType.MEGAPHONE);
			setFieldWithReflection(receipt, "item", item);
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			// when, then
			itemService.checkItemType(receipt, receipt.getItem().getType());
		}

		@Test
		@DisplayName("Receipt Not Owner Exception")
		void receiptNotOwnerTest() {
			// given
			// when, then
			assertThatThrownBy(() -> itemService.checkItemType(receipt, ItemType.EDGE))
				.isInstanceOf(ItemTypeException.class);
		}
	}

	@Nested
	@DisplayName("checkItemStatus method unitTest")
	class CheckItemStatusTest {
		Receipt receipt;

		@BeforeEach
		void beforeEach() {
			receipt = new Receipt();
			Item item = new Item();
			setFieldWithReflection(item, "type", ItemType.MEGAPHONE);
			setFieldWithReflection(receipt, "status", ItemStatus.BEFORE);
			setFieldWithReflection(receipt, "item", item);
		}

		@Test
		@DisplayName("success Megaphone1")
		void successMegaphone1() {
			// given
			setFieldWithReflection(receipt, "status", ItemStatus.USING);
			// when, then
			itemService.checkItemStatus(receipt);
		}

		@Test
		@DisplayName("success Megaphone2")
		void successMegaphone2() {
			// given
			setFieldWithReflection(receipt, "status", ItemStatus.WAITING);
			// when, then
			itemService.checkItemStatus(receipt);
		}

		@Test
		@DisplayName("success Megaphone")
		void successNotMegaphone() {
			// given
			setFieldWithReflection(receipt.getItem(), "type", ItemType.EDGE);
			// when, then
			itemService.checkItemStatus(receipt);
		}

		@Test
		@DisplayName("Receipt Not Owner Exception")
		void failMegaphone() {
			// given
			// when, then
			assertThatThrownBy(() -> itemService.checkItemStatus(receipt))
				.isInstanceOf(ItemStatusException.class);
		}

		@Test
		@DisplayName("Receipt Not Owner Exception")
		void failNotMegaphone() {
			// given
			setFieldWithReflection(receipt.getItem(), "type", ItemType.EDGE);
			setFieldWithReflection(receipt, "status", ItemStatus.USING);
			// when, then
			assertThatThrownBy(() -> itemService.checkItemStatus(receipt))
				.isInstanceOf(ItemStatusException.class);
		}
	}
}
