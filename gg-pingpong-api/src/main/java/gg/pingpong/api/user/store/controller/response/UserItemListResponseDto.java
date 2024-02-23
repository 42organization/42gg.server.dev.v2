package gg.pingpong.api.user.store.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserItemListResponseDto {
	private List<UserItemResponseDto> storageItemList;
	private Integer totalPage;

	public UserItemListResponseDto(List<UserItemResponseDto> storageItemList, Integer totalPage) {
		this.storageItemList = storageItemList;
		this.totalPage = totalPage;
	}

}
