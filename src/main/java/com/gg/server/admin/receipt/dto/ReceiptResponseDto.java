package com.gg.server.admin.receipt.dto;

import java.time.LocalDateTime;

import com.gg.server.data.store.Item;
import com.gg.server.data.store.Receipt;
import com.gg.server.data.store.type.ItemStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponseDto {
	private Long receiptId;
	private LocalDateTime createdAt;
	private String itemName;
	private Integer itemPrice;
	private String purchaserIntraId;
	private String ownerIntraId;
	private ItemStatus itemStatusType;

	public ReceiptResponseDto(Receipt receipt) {
		Item item = receipt.getItem();
		this.itemName = item.getName();
		this.itemPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
		this.receiptId = receipt.getId();
		this.createdAt = receipt.getCreatedAt();
		this.purchaserIntraId = receipt.getPurchaserIntraId();
		this.ownerIntraId = receipt.getOwnerIntraId();
		this.itemStatusType = receipt.getStatus();
	}

	@Override
	public String toString() {
		return "ReceiptResponseDto{"
			+ "receiptId=" + receiptId
			+ ", createdAt=" + createdAt
			+ ", itemName='" + itemName + '\''
			+ ", itemPrice=" + itemPrice
			+ ", purchaserIntraId='" + purchaserIntraId + '\''
			+ ", ownerIntraId='" + ownerIntraId + '\''
			+ ", itemStatusType=" + itemStatusType
			+ '}';
	}
}
