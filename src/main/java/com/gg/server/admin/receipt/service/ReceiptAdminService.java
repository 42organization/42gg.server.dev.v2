package com.gg.server.admin.receipt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.receipt.data.ReceiptAdminRepository;
import com.gg.server.admin.receipt.dto.ReceiptListResponseDto;
import com.gg.server.admin.receipt.dto.ReceiptResponseDto;
import com.gg.server.data.store.Receipt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptAdminService {
	private final ReceiptAdminRepository receiptAdminRepository;

	/**
	 * <p>모든 영수증 데이터를 가져옵니다.</p>
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReceiptListResponseDto getAllReceipt(Pageable pageable) {
		Page<ReceiptResponseDto> responseDto = receiptAdminRepository.findAll(pageable).map(ReceiptResponseDto::new);
		return new ReceiptListResponseDto(responseDto.getContent(), responseDto.getTotalPages());
	}

	/**
	 * <p>특정 유저가 사용한 영수증 내역을 가져옵니다.</p>
	 * @param intraId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReceiptListResponseDto findByIntraId(String intraId, Pageable pageable) {
		Page<Receipt> receipts = receiptAdminRepository.findReceiptByIntraId(intraId, pageable);
		Page<ReceiptResponseDto> responseDto = receipts.map(ReceiptResponseDto::new);
		return new ReceiptListResponseDto(responseDto.getContent(), responseDto.getTotalPages());
	}
}
