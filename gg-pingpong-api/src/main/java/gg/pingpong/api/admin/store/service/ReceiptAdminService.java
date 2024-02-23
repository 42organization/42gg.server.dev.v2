package gg.pingpong.api.admin.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.store.ReceiptAdminRepository;
import gg.pingpong.api.admin.store.controller.response.ReceiptListResponseDto;
import gg.pingpong.api.admin.store.controller.response.ReceiptResponseDto;
import gg.pingpong.data.store.Receipt;
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
