package gg.pingpong.api.admin.store.controller.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemListResponseDto {
	private List<ItemHistoryResponseDto> historyList;
	private Integer totalPage;
}
