package com.gg.server.admin.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull(message = "plz. itemName")
    private String name;

    @NotNull(message = "plz. content")
    private String content;

    @NotNull(message = "plz. imageUri")
    private String imageUri;

    @NotNull(message = "plz. price")
    private Integer price;

    @NotNull(message = "plz. discount")
    private Integer discount;

    @Override
    public String toString() {
        return "ItemRequestDto{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }
}
