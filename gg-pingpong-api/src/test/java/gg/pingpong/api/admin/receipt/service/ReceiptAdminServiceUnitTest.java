package gg.pingpong.api.admin.receipt.service;

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

import gg.admin.repo.store.ReceiptAdminRepository;
import gg.data.store.Receipt;
import gg.pingpong.api.admin.store.service.ReceiptAdminService;
import gg.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ReceiptAdminServiceUnitTest {
	@Mock
	ReceiptAdminRepository receiptAdminRepository;
	@InjectMocks
	ReceiptAdminService receiptAdminService;

	@Nested
	@DisplayName("getAllReceipt 메서드 유닛 테스트")
	class GetAllReceipt {
		@Test
		@DisplayName("success")
		void success() {
			// given
			ArrayList<Receipt> receipts = new ArrayList<>();
			given(receiptAdminRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(receipts));
			// when, then
			receiptAdminService.getAllReceipt(mock(Pageable.class));
		}
	}

	@Nested
	@DisplayName("findByIntraId 메서드 유닛 테스트")
	class FindByIntraId {
		@Test
		@DisplayName("success")
		void success() {
			// given
			ArrayList<Receipt> receipts = new ArrayList<>();
			given(receiptAdminRepository.findReceiptByIntraId(any(String.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(receipts));
			// when, then
			receiptAdminService.findByIntraId("intraId", mock(Pageable.class));
		}
	}
}
