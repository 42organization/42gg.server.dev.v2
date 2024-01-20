package com.gg.server.admin.receipt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.receipt.data.ReceiptAdminRepository;
import com.gg.server.admin.receipt.dto.ReceiptListResponseDto;
import com.gg.server.admin.receipt.dto.ReceiptResponseDto;
import com.gg.server.domain.receipt.data.Receipt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptAdminService {
	private final ReceiptAdminRepository receiptAdminRepository;

	@Transactional(readOnly = true)
	public ReceiptListResponseDto getAllReceipt(Pageable pageable) {
		Page<ReceiptResponseDto> responseDtos = receiptAdminRepository.findAll(pageable).map(ReceiptResponseDto::new);
		return new ReceiptListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
	}

	@Transactional(readOnly = true)
	public ReceiptListResponseDto findByIntraId(String intraId, Pageable pageable) {
		Page<Receipt> receipts = receiptAdminRepository.findReceiptByIntraId(intraId, pageable);
		Page<ReceiptResponseDto> responseDtos = receipts.map(ReceiptResponseDto::new);
		return new ReceiptListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
	}
}
