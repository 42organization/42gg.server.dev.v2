package com.gg.server.domain.item.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Boolean isVisible;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

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
}