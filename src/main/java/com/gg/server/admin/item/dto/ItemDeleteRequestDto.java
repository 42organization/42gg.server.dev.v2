package com.gg.server.admin.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDeleteRequestDto {
    @NotNull(message = "plz. deleterIntraId")
    private String deleterIntraId;

    @Override
    public String toString() {
        return "ItemDeleteRequestDto{" +
                "name='" + deleterIntraId + '\'' +
                '}';
    }
}
