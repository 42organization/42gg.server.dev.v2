package gg.pingpong.api.admin.item.service;

import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;

import gg.admin.repo.store.ItemAdminRepository;
import gg.data.pingpong.store.Item;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.admin.store.controller.request.ItemUpdateRequestDto;
import gg.pingpong.api.admin.store.service.ItemAdminService;
import gg.pingpong.api.global.utils.aws.AsyncNewItemImageUploader;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.item.ItemNotAvailableException;
import gg.utils.exception.item.ItemNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ItemAdminServiceUnitTest {
	@Mock
	ItemAdminRepository itemAdminRepository;
	@Mock
	AsyncNewItemImageUploader asyncNewItemImageUploader;

	@InjectMocks
	ItemAdminService itemAdminService;

	User user;
	Item item;

	@BeforeEach
	void beforeEach() {
		item = new Item();
		setFieldWithReflection(item, "isVisible", true);
		user = new User("testUser", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
	}

	@Nested
	@DisplayName("getAllItemHistoryTest")
	class GetAllItemHistoryTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(itemAdminRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(new ArrayList<>()));
			// when, then
			itemAdminService.getAllItemHistory(mock(Pageable.class));
			verify(itemAdminRepository, times(1)).findAll(any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("updateItemTest 파라미터 4개 짜리")
	class UpdateItem4ParamsTest {
		@Test
		@DisplayName("success")
		void success() throws Exception {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(itemAdminRepository.save(any(Item.class))).willReturn(mock(Item.class));
			ItemUpdateRequestDto mockRequest = mock(ItemUpdateRequestDto.class);
			when(mockRequest.toItem(any(), any())).thenReturn(mock(Item.class));

			// when, then
			itemAdminService.updateItem(1L, mockRequest,
				mock(MultipartFile.class), UserDto.from(user));
			setFieldWithReflection(item, "isVisible", true);

			itemAdminService.updateItem(1L, mockRequest,
				null, UserDto.from(user));
			verify(itemAdminRepository, times(2)).findById(any(Long.class));
			verify(asyncNewItemImageUploader, times(1)).upload(any(), any());
			verify(itemAdminRepository, times(2)).save(any(Item.class));
		}

		@Test
		@DisplayName("ItemNotAvailableTest")
		void itemNotAvailableTest() throws Exception {
			// given
			setFieldWithReflection(item, "isVisible", false);
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			// when, then
			assertThatThrownBy(
				() -> itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), mock(MultipartFile.class),
					UserDto.from(user)))
				.isInstanceOf(ItemNotAvailableException.class);
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("ItemNotFoundTest")
		void itemNotFoundTest() throws Exception {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(
				() -> itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), mock(MultipartFile.class),
					UserDto.from(user)))
				.isInstanceOf(ItemNotFoundException.class);
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("updateItemTest 파라미터 3개 짜리")
	class UpdateItem3ParamsTest {
		@Test
		@DisplayName("success")
		void success() throws Exception {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			given(itemAdminRepository.save(any(Item.class))).willReturn(mock(Item.class));
			ItemUpdateRequestDto mockRequest = mock(ItemUpdateRequestDto.class);
			when(mockRequest.toItem(any(), any())).thenReturn(mock(Item.class));

			// when, then
			itemAdminService.updateItem(1L, mockRequest, UserDto.from(user));
			assertThat(item.getDeleterIntraId()).isEqualTo(user.getIntraId());
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
			verify(itemAdminRepository, times(1)).save(any(Item.class));
		}

		@Test
		@DisplayName("ItemNotAvailableTest")
		void itemNotAvailableTest() throws Exception {
			// given
			setFieldWithReflection(item, "isVisible", false);
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			// when, then
			assertThatThrownBy(
				() -> itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), UserDto.from(user)))
				.isInstanceOf(ItemNotAvailableException.class);
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("ItemNotFoundTest")
		void itemNotFoundTest() throws Exception {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(
				() -> itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), UserDto.from(user)))
				.isInstanceOf(ItemNotFoundException.class);
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	@DisplayName("deleteItemTest")
	class DeleteItemTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
			// when, then
			itemAdminService.deleteItem(1L, UserDto.from(user));
			assertThat(item.getIsVisible()).isFalse();
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}

		@Test
		@DisplayName("ItemNotFoundTest")
		void itemNotFoundTest() {
			// given
			given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> itemAdminService.deleteItem(1L, UserDto.from(user)))
				.isInstanceOf(ItemNotFoundException.class);
			verify(itemAdminRepository, times(1)).findById(any(Long.class));
		}
	}
}
