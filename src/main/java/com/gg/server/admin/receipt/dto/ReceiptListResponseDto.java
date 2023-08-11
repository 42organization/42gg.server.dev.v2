package com.gg.server.admin.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptListResponseDto {
    List<ReceiptResponseDto> receiptList;
    Integer totalPage;
}
