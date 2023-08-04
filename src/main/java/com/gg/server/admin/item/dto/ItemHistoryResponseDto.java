package com.gg.server.admin.item.dto;

import com.gg.server.domain.item.data.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemHistoryResponseDto {
    private Long itemId;
    private String name;
    private String content;
    private String imageUri;
    private Integer price;
    private Integer discount;
    private boolean isVisible;
    private LocalDateTime createdAt;

    public ItemHistoryResponseDto(Item item) {
        this.itemId = item.getId();
        this.name = item.getName();
        this.content = item.getContent();
        this.imageUri = item.getImageUri();
        this.price = item.getPrice();
        this.discount = item.getDiscount();
        this.isVisible = item.getIsVisible();
        this.createdAt = item.getCreatedAt();
    }

    @Override
    public String toString() {
        return "ItemHistoryResponseDto{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", isVisible=" + isVisible +
                ", createdAt=" + createdAt +
                '}';
    }
}
