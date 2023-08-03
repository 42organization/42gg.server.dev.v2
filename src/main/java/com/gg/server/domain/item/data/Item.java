package com.gg.server.domain.item.data;

import com.gg.server.admin.item.dto.ItemRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "content", length = 255)
    private String content;

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
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Item(String name, String content, String imageUri, Integer price,
                Boolean isVisible, Integer discount, LocalDateTime createdAt) {
        this.name = name;
        this.content = content;
        this.imageUri = imageUri;
        this.price = price;
        this.isVisible = isVisible;
        this.discount = discount;
        this.createdAt = createdAt;
    }

    @Builder
    public Item(ItemRequestDto createDto) {
        this.name = createDto.getName();
        this.content = createDto.getContent();
        this.imageUri = createDto.getImageUri();
        this.price = createDto.getPrice();
        this.discount = createDto.getDiscount();
        this.isVisible = true;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", price=" + price +
                ", isVisible=" + isVisible +
                ", discount=" + discount +
                ", createdAt=" + createdAt +
                '}';
    }
}