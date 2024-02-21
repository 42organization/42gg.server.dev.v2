package gg.pingpong.data.store;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import gg.pingpong.data.store.type.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 30)
	private String name;

	@Column(name = "main_content", length = 255)
	private String mainContent;

	@Column(name = "sub_content", length = 255)
	private String subContent;

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
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private ItemType type;

	@NotNull
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@NotNull
	@Column(name = "creator_intra_id", length = 10)
	private String creatorIntraId;

	@Column(name = "deleter_intra_id", length = 10)
	private String deleterIntraId;

	public Item(String name, String mainContent, String subContent, String imageUri, Integer price,
		Boolean isVisible, Integer discount, ItemType type, LocalDateTime createdAt, String creatorIntraId) {
		this.name = name;
		this.mainContent = mainContent;
		this.subContent = subContent;
		this.imageUri = imageUri;
		this.price = price;
		this.isVisible = isVisible;
		this.discount = discount;
		this.type = type;
		this.createdAt = createdAt;
		this.creatorIntraId = creatorIntraId;
	}

	public void imageUpdate(String imageUri) {
		this.imageUri = imageUri;
	}

	public void setVisibility(String intraId) {
		this.isVisible = false;
		this.deleterIntraId = intraId;
	}

	@Override
	public String toString() {
		return "Item{"
			+ "id=" + id
			+ ", name='" + name + '\''
			+ ", mainContent='" + mainContent + '\''
			+ ", subContent='" + subContent + '\''
			+ ", imageUri='" + imageUri + '\''
			+ ", price=" + price
			+ ", isVisible=" + isVisible
			+ ", discount=" + discount
			+ ", createdAt=" + createdAt
			+ ", creatorIntraId='" + creatorIntraId + '\''
			+ ", deleterIntraId='" + deleterIntraId + '\''
			+ '}';
	}
}
