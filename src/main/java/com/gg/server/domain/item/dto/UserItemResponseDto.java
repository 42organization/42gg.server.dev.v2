package com.gg.server.domain.item.dto;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.type.ItemType;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.type.ItemStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserItemResponseDto {
	private Long receiptId;
	private String itemName;
	private String imageUri;
	private String purchaserIntra;
	private ItemStatus itemStatus;
	private ItemType itemType;

	public UserItemResponseDto(Receipt receipt) {
		Item item = receipt.getItem();
		this.receiptId = receipt.getId();
		this.itemName = item.getName();
		this.imageUri = item.getImageUri();
		this.purchaserIntra = receipt.getPurchaserIntraId();
		this.itemStatus = receipt.getStatus();
		this.itemType = item.getType();
	}

}
