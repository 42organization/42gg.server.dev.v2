package gg.pingpong.api.admin.megaphone.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import gg.pingpong.admin.repo.megaphone.MegaphoneAdminRepository;
import gg.pingpong.api.admin.store.service.MegaphoneAdminService;
import gg.pingpong.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class MegaphoneAdminServiceUnitTest {
	@Mock
	MegaphoneAdminRepository megaphoneAdminRepository;
	@InjectMocks
	MegaphoneAdminService megaphoneAdminService;

	@Nested
	@DisplayName("getMegaphoneHistory 메서드 유닛 테스트")
	class GetMegaphoneHistory {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(megaphoneAdminRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(new ArrayList<>()));
			// when, then
			megaphoneAdminService.getMegaphoneHistory(mock(Pageable.class));
			verify(megaphoneAdminRepository, times(1)).findAll(any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("getMegaphoneHistoryByIntraId 메서드 유닛 테스트")
	class GetMegaphoneHistoryByIntraId {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(megaphoneAdminRepository.findMegaphonesByUserIntraId(any(String.class), any(
				Pageable.class))).willReturn(new PageImpl<>(new ArrayList<>()));
			// when, then
			megaphoneAdminService.getMegaphoneHistoryByIntraId("testUser", mock(Pageable.class));
			verify(megaphoneAdminRepository, times(1))
				.findMegaphonesByUserIntraId(any(String.class), any(Pageable.class));
		}
	}
}
