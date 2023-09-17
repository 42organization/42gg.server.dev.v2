package com.gg.server.domain.item.data;

import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.domain.item.type.ItemType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "main_content", length = 255)
    private String mainContent;

    @Column(name = "sub_content", length = 255)
    private String subContent;

    @Column(name = "image_uri", length = 255)
    private String imageUri;

    @NotNull
    @Column(name = "price")
    private Integer price;

    @NotNull
    @Column(name = "is_visible")
    @Setter
    private Boolean isVisible;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ItemType type;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "creator_intra_id", length = 10)
    private String creatorIntraId;

    @Column(name = "deleter_intra_id", length = 10)
    @Setter
    private String deleterIntraId;

    public Item(String name, String mainContent, String subContent, String imageUri, Integer price,
                Boolean isVisible, Integer discount, ItemType type, LocalDateTime createdAt) {
        this.name = name;
        this.mainContent = mainContent;
        this.subContent = subContent;
        this.imageUri = imageUri;
        this.price = price;
        this.isVisible = isVisible;
        this.discount = discount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Item(String name, String mainContent, String subContent, String imageUri, Integer price,
                Boolean isVisible, Integer discount, ItemType type, LocalDateTime createdAt, String creatorIntraId) {
        this.name = name;
        this.mainContent = mainContent;
        this.subContent = subContent;
        this.imageUri = imageUri;
        this.price = price;
        this.isVisible = isVisible;
        this.discount = discount;
        this.type = type;
        this.createdAt = createdAt;
        this.creatorIntraId = creatorIntraId;
    }

    @Builder
    public Item(ItemUpdateRequestDto updateRequestDto, String creatorIntraId, String itemImageUri) {
        this.name = updateRequestDto.getName();
        this.mainContent = updateRequestDto.getMainContent();
        this.subContent = updateRequestDto.getSubContent();
        this.imageUri = itemImageUri;
        this.price = updateRequestDto.getPrice();
        this.discount = updateRequestDto.getDiscount();
        this.isVisible = true;
        this.creatorIntraId = creatorIntraId;
        this.createdAt = LocalDateTime.now();
        this.type = updateRequestDto.getItemType();
    }

    @Builder
    public Item(ItemUpdateRequestDto updateRequestDto, String creatorIntraId) {
        this.name = updateRequestDto.getName();
        this.mainContent = updateRequestDto.getMainContent();
        this.subContent = updateRequestDto.getSubContent();
        this.price = updateRequestDto.getPrice();
        this.discount = updateRequestDto.getDiscount();
        this.isVisible = true;
        this.creatorIntraId = creatorIntraId;
        this.createdAt = LocalDateTime.now();
        this.type = updateRequestDto.getItemType();
    }

    public void imageUpdate(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mainContent='" + mainContent + '\'' +
                ", subContent='" + subContent + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", price=" + price +
                ", isVisible=" + isVisible +
                ", discount=" + discount +
                ", createdAt=" + createdAt +
                ", creatorIntraId='" + creatorIntraId + '\'' +
                ", deleterIntraId='" + deleterIntraId + '\'' +
                '}';
    }
}