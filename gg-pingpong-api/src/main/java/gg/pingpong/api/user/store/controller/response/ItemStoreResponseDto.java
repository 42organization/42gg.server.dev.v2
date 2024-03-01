package gg.pingpong.api.user.store.controller.response;

import gg.data.store.Item;
import gg.data.store.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemStoreResponseDto {
	private Long itemId;
	private String itemName;
	private String mainContent;
	private String subContent;
	private ItemType itemType;
	private String imageUri;
	private Integer originalPrice;
	private Integer discount;
	private Integer salePrice;

	public ItemStoreResponseDto(Item item) {
		this.itemId = item.getId();
		this.itemName = item.getName();
		this.mainContent = item.getMainContent();
		this.subContent = item.getSubContent();
		this.itemType = item.getType();
		this.imageUri = item.getImageUri();
		this.originalPrice = item.getPrice();
		this.discount = item.getDiscount();
		this.salePrice = this.originalPrice - (this.originalPrice * this.discount / 100);
	}
}
