package gg.pingpong.api.admin.store.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.store.controller.request.ReceiptAdminPageRequestDto;
import gg.pingpong.api.admin.store.controller.response.ReceiptListResponseDto;
import gg.pingpong.api.admin.store.service.ReceiptAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/receipt")
public class ReceiptAdminController {
	private final ReceiptAdminService receiptAdminService;

	@GetMapping
	public ReceiptListResponseDto getReceiptList(@ModelAttribute @Valid ReceiptAdminPageRequestDto req) {

		if (req.getIntraId() == null) {
			Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(),
				Sort.by("createdAt").descending());
			return receiptAdminService.getAllReceipt(pageable);
		}
		Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(),
			Sort.by("createdAt").descending());
		return receiptAdminService.findByIntraId(req.getIntraId(), pageable);
	}
}
