package com.gg.server.domain.item.dto;

import com.gg.server.domain.item.data.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemStoreResponseDto {
    private Long itemId;
    private String itemName;
    private String content;
    private String itemType;
    private String imageUri;
    private Integer originalPrice;
    private Integer discount;
    private Integer salePrice;

    public ItemStoreResponseDto(Item item) {
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.content = item.getContent();
        this.itemType = item.getType().toString();
        this.imageUri = item.getImageUri();
        this.originalPrice = item.getPrice();
        this.discount = item.getDiscount();
        this.salePrice = this.originalPrice - (this.originalPrice * this.discount / 100);
    }
}
