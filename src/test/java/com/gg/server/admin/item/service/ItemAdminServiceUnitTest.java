package com.gg.server.admin.item.service;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.setFieldWithReflection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gg.server.admin.item.data.ItemAdminRepository;
import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.admin.item.exception.ItemNotFoundException;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.exception.ItemNotAvailableException;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.utils.aws.AsyncNewItemImageUploader;
import com.gg.server.utils.annotation.UnitTest;
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
            // when, then
            itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), mock(MultipartFile.class), UserDto.from(user));
            setFieldWithReflection(item, "isVisible", true);
            itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), null, UserDto.from(user));
        }
        @Test
        @DisplayName("ItemNotAvailableTest")
        void itemNotAvailableTest() throws Exception {
            // given
            setFieldWithReflection(item, "isVisible", false);
            given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
            // when, then

            assertThatThrownBy(()->itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), mock(MultipartFile.class), UserDto.from(user)))
                .isInstanceOf(ItemNotAvailableException.class);
        }
        @Test
        @DisplayName("ItemNotFoundTest")
        void ItemNotFoundTest() throws Exception {
            // given
            given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then

            assertThatThrownBy(()->itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), mock(MultipartFile.class), UserDto.from(user)))
                .isInstanceOf(ItemNotFoundException.class);
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
            // when, then
            itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), UserDto.from(user));
        }
        @Test
        @DisplayName("ItemNotAvailableTest")
        void itemNotAvailableTest() throws Exception {
            // given
            setFieldWithReflection(item, "isVisible", false);
            given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.of(item));
            // when, then

            assertThatThrownBy(()->itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), UserDto.from(user)))
                .isInstanceOf(ItemNotAvailableException.class);
        }
        @Test
        @DisplayName("ItemNotFoundTest")
        void ItemNotFoundTest() throws Exception {
            // given
            given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then

            assertThatThrownBy(()->itemAdminService.updateItem(1L, mock(ItemUpdateRequestDto.class), UserDto.from(user)))
                .isInstanceOf(ItemNotFoundException.class);
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
        }
        @Test
        @DisplayName("ItemNotFoundTest")
        void itemNotFoundTest() {
            // given
            given(itemAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->itemAdminService.deleteItem(1L, UserDto.from(user)))
                .isInstanceOf(ItemNotFoundException.class);
        }
    }
}