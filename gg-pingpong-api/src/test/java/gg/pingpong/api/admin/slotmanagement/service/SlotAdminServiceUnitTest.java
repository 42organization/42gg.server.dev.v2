package gg.pingpong.api.admin.slotmanagement.service;

import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import gg.admin.repo.manage.AdminSlotManagementsRepository;
import gg.data.pingpong.manage.SlotManagement;
import gg.pingpong.api.admin.manage.controller.request.SlotCreateRequestDto;
import gg.pingpong.api.admin.manage.service.SlotAdminService;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.slotmanagement.SlotManagementForbiddenException;

@UnitTest
class SlotAdminServiceUnitTest {
	@Mock
	AdminSlotManagementsRepository adminSlotManagementRepository;
	@InjectMocks
	SlotAdminService slotAdminService;

	List<SlotManagement> slotManagementList;
	List<SlotCreateRequestDto> requestDtoList;

	@BeforeEach
	void beforeEach() {
		slotManagementList = new ArrayList<>();
		slotManagementList.add(new SlotManagement(12, 11, 5, 15,
			LocalDateTime.now().plusHours(1), null));
		requestDtoList = new ArrayList<>();
		// success case
		requestDtoList.add(new SlotCreateRequestDto(12, 11, 15, 5,
			LocalDateTime.now().plusDays(1)));
		// Exception case
		requestDtoList.add(new SlotCreateRequestDto(24, 11, 15, 5,
			LocalDateTime.now().plusHours(1)));
		requestDtoList.add(new SlotCreateRequestDto(12, 13, 15, 5,
			LocalDateTime.now()));
		requestDtoList.add(new SlotCreateRequestDto(12, 11, 13, 5,
			LocalDateTime.now()));
		requestDtoList.add(new SlotCreateRequestDto(12, 11, 12, 5,
			LocalDateTime.now()));
		requestDtoList.add(new SlotCreateRequestDto(15, 11, 10, 5,
			LocalDateTime.now()));
	}

	@Nested
	@DisplayName("getSlotSetting 매서드 단위 테스트")
	@MockitoSettings(strictness = Strictness.LENIENT)
	class GetSlotSetting {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(adminSlotManagementRepository.findAfterNowSlotManagement(any()))
				.willReturn(slotManagementList);
			// when, then
			slotAdminService.getSlotSetting();

			verify(adminSlotManagementRepository, times(1)).findAfterNowSlotManagement(any());
		}
	}

	@Nested
	@DisplayName("addSlotSetting 매서드 단위 테스트")
	@MockitoSettings(strictness = Strictness.LENIENT)
	class AddSlotSetting {
		@BeforeEach
		void beforeEach() {
			// given
			given(adminSlotManagementRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.of(slotManagementList.get(0)));
			given(adminSlotManagementRepository.save(any())).willReturn(mock(SlotManagement.class));
		}

		@Test
		@DisplayName("success")
		void success() {
			// when, then
			slotAdminService.addSlotSetting(requestDtoList.get(0));
			verify(adminSlotManagementRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("checkVaildSlotManagement에서 발생하는 Exception")
		void checkVaildSlotManagement() {
			//when, then
			for (int i = 1; i < requestDtoList.size(); i++) {
				int idx = i;
				assertThatThrownBy(() -> slotAdminService.addSlotSetting(requestDtoList.get(idx)))
					.isInstanceOf(SlotManagementForbiddenException.class);
			}
		}

		@Test
		@DisplayName("updateNowSlotManagementEndTime에서 발생하는 Exception")
		void updateNowSlotManagementEndTime() {
			//when, then
			setFieldWithReflection(slotManagementList.get(0), "endTime", LocalDateTime.now());
			assertThatThrownBy(() -> slotAdminService.addSlotSetting(requestDtoList.get(0)))
				.isInstanceOf(SlotManagementForbiddenException.class);
			setFieldWithReflection(slotManagementList.get(0), "startTime", LocalDateTime.now());
			setFieldWithReflection(requestDtoList.get(0), "startTime", LocalDateTime.now());
			assertThatThrownBy(() -> slotAdminService.addSlotSetting(requestDtoList.get(0)))
				.isInstanceOf(SlotManagementForbiddenException.class);
		}
	}

	@Nested
	@DisplayName("delSlotSetting 매서드 단위 테스트")
	@MockitoSettings(strictness = Strictness.LENIENT)
	class DelSlotSetting {
		@BeforeEach
		void beforeEach() {
			// given
			slotManagementList.add(
				new SlotManagement(12, 11, 5, 15,
					LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
			given(adminSlotManagementRepository.findAfterNowSlotManagement(any()))
				.willReturn(slotManagementList);
		}

		@Test
		@DisplayName("success")
		void success() {
			// when, then
			slotAdminService.delSlotSetting();
			verify(adminSlotManagementRepository, times(1)).findAfterNowSlotManagement(any());
			verify(adminSlotManagementRepository, times(1)).delete(any());
		}

		@Test
		@DisplayName("SlotManagementForbiddenException")
		void slotManagementForbiddenException() {
			// when, then
			setFieldWithReflection(slotManagementList.get(1), "endTime", null);
			assertThatThrownBy(() -> slotAdminService.delSlotSetting())
				.isInstanceOf(SlotManagementForbiddenException.class);
			setFieldWithReflection(slotManagementList.get(0), "startTime", LocalDateTime.now());
			assertThatThrownBy(() -> slotAdminService.delSlotSetting())
				.isInstanceOf(SlotManagementForbiddenException.class);
		}
	}
}
