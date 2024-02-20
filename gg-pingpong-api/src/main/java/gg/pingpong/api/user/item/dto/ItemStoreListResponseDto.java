package gg.pingpong.api.user.item.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ItemStoreListResponseDto {
	private List<ItemStoreResponseDto> itemList;

	public ItemStoreListResponseDto(List<ItemStoreResponseDto> itemList) {
		this.itemList = itemList;
	}
}