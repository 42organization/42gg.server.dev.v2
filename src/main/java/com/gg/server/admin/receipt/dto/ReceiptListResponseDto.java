package com.gg.server.admin.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptListResponseDto {
    private List<ReceiptResponseDto> receiptList;
    private Integer totalPage;
}
