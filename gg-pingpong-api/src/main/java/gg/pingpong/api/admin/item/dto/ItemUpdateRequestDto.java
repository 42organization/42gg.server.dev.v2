package gg.pingpong.api.admin.item.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import gg.pingpong.data.store.Item;
import gg.pingpong.data.store.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateRequestDto {
	@NotNull(message = "[Request] 아이템 이름은 Null 일 수 없습니다.")
	private String name;

	@NotNull(message = "[Request] 주 설명은 Null 일 수 없습니다.")
	private String mainContent;

	@NotNull(message = "[Request] 부 설명은 Null 일 수 없습니다.")
	private String subContent;

	@NotNull(message = "[Request] 가격은 Null 일 수 없습니다.")
	private Integer price;

	@NotNull(message = "[Request] 할인율은 Null 일 수 없습니다.")
	private Integer discount;

	@NotNull(message = "[Request] 아이템 타입은 Null 일 수 없습니다.")
	private ItemType itemType;

	public Item toItem(String imageUrl, String userIntraId) {
		return Item.builder()
			.name(name)
			.mainContent(mainContent)
			.subContent(subContent)
			.price(price)
			.discount(discount)
			.imageUri(imageUrl)
			.isVisible(true)
			.type(itemType)
			.createdAt(LocalDateTime.now())
			.creatorIntraId(userIntraId)
			.build();
	}

	@Override
	public String toString() {
		return "ItemUpdateRequestDto{"
			+ "name='" + name + '\''
			+ ", mainContent='" + mainContent + '\''
			+ ", subContent='" + subContent + '\''
			+ ", price=" + price
			+ ", discount=" + discount
			+ ", itemType='" + itemType + '\''
			+ '}';
	}
}
