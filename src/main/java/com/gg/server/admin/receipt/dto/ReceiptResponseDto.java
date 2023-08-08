package com.gg.server.admin.receipt.dto;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.receipt.data.Receipt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Enum itemStatusType;

    public ReceiptResponseDto(Receipt receipt) {
        Item item = receipt.getItem();
        this.itemName = item.getName();
        this.itemPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
        this.receiptId = receipt.getId();
        this.createdAt = receipt.getPurchasedAt();
        this.purchaserIntraId = receipt.getPurchaserIntraId();
        this.ownerIntraId = receipt.getOwnerIntraId();
        this.itemStatusType = receipt.getStatus();
    }
}
