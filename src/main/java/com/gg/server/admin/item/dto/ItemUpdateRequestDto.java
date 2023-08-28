package com.gg.server.admin.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateRequestDto {
    @NotNull(message = "plz. itemName")
    private String name;

    @NotNull(message = "plz. mainContent")
    private String mainContent;

    @NotNull(message = "plz. subContent")
    private String subContent;

    @NotNull(message = "plz. imageUri")
    private String imageUri;

    @NotNull(message = "plz. price")
    private Integer price;

    @NotNull(message = "plz. discount")
    private Integer discount;

    @NotNull(message = "plz. creatorIntraId")
    private String creatorIntraId;

    @Override
    public String toString() {
        return "ItemRequestDto{" +
                "name='" + name + '\'' +
                ", mainContent='" + mainContent + '\'' +
                ", subContent='" + subContent + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", creator='" + creatorIntraId + '\'' +
                '}';
    }
}
