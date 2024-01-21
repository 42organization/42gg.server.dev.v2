package com.gg.server.domain.megaphone.service;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.setFieldWithReflection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.item.type.ItemType;
import com.gg.server.domain.megaphone.data.Megaphone;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.exception.MegaphoneContentException;
import com.gg.server.domain.megaphone.exception.MegaphoneNotFoundException;
import com.gg.server.domain.megaphone.exception.MegaphoneTimeException;
import com.gg.server.domain.megaphone.redis.MegaphoneRedisRepository;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ItemStatusException;
import com.gg.server.domain.receipt.exception.ReceiptNotFoundException;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.utils.annotation.UnitTest;
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
        user = new User("", "", "", RacketType.NONE, RoleType.USER, 0, SnsType.NONE, 1L);
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
            megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(54));
            setFieldWithReflection(receipt, "status", ItemStatus.BEFORE);
            megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(0).withMinute(6));

            // then
            assertThat(receipt.getStatus()).isEqualTo(ItemStatus.WAITING);

        }
        @Test
        @DisplayName("MegaphoneContentException")
        void megaphoneContentException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
            setFieldWithReflection(megaphoneUseRequestDto, "content", "");
            // when, then

            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(54)))
                .isInstanceOf(MegaphoneContentException.class);
        }
        @Test
        @DisplayName("ItemStatusException")
        void itemStatusException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
            setFieldWithReflection(receipt, "status", ItemStatus.USING);
            // when, then
            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(54)))
                .isInstanceOf(ItemStatusException.class);
        }
        @Test
        @DisplayName("ReceiptNotFoundException")
        void receiptNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(receiptRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(54)))
                .isInstanceOf(ReceiptNotFoundException.class);
        }
        @Test
        @DisplayName("MegaphoneTimeException")
        void megaphoneTimeException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            // when, then
            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(56)))
                .isInstanceOf(MegaphoneTimeException.class);
            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(0).withMinute(3)))
                .isInstanceOf(MegaphoneTimeException.class);
        }
        @Test
        @DisplayName("UserNotFoundException")
        void userNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            Assertions.assertThatThrownBy(()->megaphoneService.useMegaphone(megaphoneUseRequestDto, UserDto.from(user), LocalTime.now().withHour(23).withMinute(56)))
                .isInstanceOf(UserNotFoundException.class);
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
            Receipt receiptUsing = new Receipt(item, "", "testUser", ItemStatus.USING, LocalDateTime.now());
            usingList.add(new Megaphone(user, receiptUsing, "test", LocalDate.now()));
        }
        @Test
        @DisplayName("success")
        void success() {
            // given
            given(megaphoneRepository.findAllByUsedAtAndReceiptStatus(LocalDate.now(),
                ItemStatus.USING))
                .willReturn(usingList);
            given(megaphoneRepository.findAllByUsedAtAndReceiptStatus(LocalDate.now().plusDays(1),
                ItemStatus.WAITING))
                .willReturn(waitList);
            // when, then
            megaphoneService.setMegaphoneList(LocalDate.now());
            assertThat(usingList.get(0).getReceipt().getStatus()).isEqualTo(ItemStatus.USED);
            assertThat(waitList.get(0).getReceipt().getStatus()).isEqualTo(ItemStatus.USING);
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
            setFieldWithReflection(receipt, "status", ItemStatus.USING);
            // when
            megaphoneService.deleteMegaphone(1L, UserDto.from(user));
            setFieldWithReflection(user, "roleType", RoleType.ADMIN);
            megaphoneService.deleteMegaphone(1L, UserDto.from(user));
            setFieldWithReflection(receipt, "status", ItemStatus.USING);
            megaphoneService.deleteMegaphone(1L, UserDto.from(user));
            // then
            assertThat(receipt.getStatus()).isEqualTo(ItemStatus.DELETED);
        }
        @Test
        @DisplayName("MegaphoneNotFoundException")
        void megaphoneNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(megaphoneRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->megaphoneService.deleteMegaphone(1L, UserDto.from(user)))
                .isInstanceOf(MegaphoneNotFoundException.class);
        }
        @Test
        @DisplayName("UserNotFoundException")
        void userNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->megaphoneService.deleteMegaphone(1L, UserDto.from(user)))
                .isInstanceOf(UserNotFoundException.class);
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
        }
        @Test
        @DisplayName("MegaphoneNotFoundException")
        void megaphoneNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(receiptRepository.findById(any(Long.class))).willReturn(Optional.of(receipt));
            given(megaphoneRepository.findByReceipt(any(Receipt.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
                .isInstanceOf(MegaphoneNotFoundException.class);
        }
        @Test
        @DisplayName("ReceiptNotFoundException")
        void receiptNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(receiptRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
                .isInstanceOf(ReceiptNotFoundException.class);
        }
        @Test
        @DisplayName("UserNotFoundException")
        void userNotFoundException() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->megaphoneService.getMegaphoneDetail(1L, UserDto.from(user)))
                .isInstanceOf(UserNotFoundException.class);
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
        }
    }
}