package com.gg.server.admin.receipt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptListResponseDto {
	private List<ReceiptResponseDto> receiptList;
	private Integer totalPage;
}
