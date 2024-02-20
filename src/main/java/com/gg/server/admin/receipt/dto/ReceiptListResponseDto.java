package com.gg.server.admin.receipt.dto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptListResponseDto {
	private List<ReceiptResponseDto> receiptList;
	private Integer totalPage;
}
