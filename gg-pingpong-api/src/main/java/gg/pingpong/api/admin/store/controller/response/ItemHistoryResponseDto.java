package gg.pingpong.api.admin.store.controller.response;

import java.time.LocalDateTime;

import gg.data.store.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemHistoryResponseDto {
	private Long itemId;
	private String name;
	private String mainContent;
	private String subContent;
	private String imageUri;
	private Integer price;
	private Integer discount;
	private boolean isVisible;
	private LocalDateTime createdAt;
	private String creatorIntraId;
	private String deleterIntraId;

	public ItemHistoryResponseDto(Item item) {
		this.itemId = item.getId();
		this.name = item.getName();
		this.mainContent = item.getMainContent();
		this.subContent = item.getSubContent();
		this.imageUri = item.getImageUri();
		this.price = item.getPrice();
		this.discount = item.getDiscount();
		this.isVisible = item.getIsVisible();
		this.createdAt = item.getCreatedAt();
		this.creatorIntraId = item.getCreatorIntraId();
		this.deleterIntraId = item.getDeleterIntraId();
	}

	@Override
	public String toString() {
		return "ItemHistoryResponseDto{"
			+ "itemId=" + itemId
			+ ", name='" + name + '\''
			+ ", mainContent='" + mainContent + '\''
			+ ", subContent='" + subContent + '\''
			+ ", imageUri='" + imageUri + '\''
			+ ", price=" + price
			+ ", discount=" + discount
			+ ", isVisible=" + isVisible
			+ ", createdAt=" + createdAt
			+ ", creatorIntraId='" + creatorIntraId + '\''
			+ ", deleterIntraId='" + deleterIntraId + '\''
			+ '}';
	}
}
