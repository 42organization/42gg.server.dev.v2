package com.gg.server.admin.receipt.controller;

import com.gg.server.admin.receipt.dto.ReceiptListResponseDto;
import com.gg.server.admin.receipt.service.ReceiptAdminService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/receipt")
public class ReceiptAdminController {
    private final ReceiptAdminService receiptAdminService;

    @GetMapping("/list")
    public ReceiptListResponseDto getReceiptList(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
                Sort.by("purchasedAt").descending());
        return receiptAdminService.getAllReceipt(pageable);
    }
}