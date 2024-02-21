package gg.pingpong.data.store;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import gg.pingpong.data.store.type.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Receipt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	@NotNull
	@Column(name = "purchaser_intra_id")
	private String purchaserIntraId;

	@NotNull
	@Column(name = "owner_intra_id")
	private String ownerIntraId;

	@NotNull
	@Enumerated(EnumType.STRING)
	private ItemStatus status;

	@NotNull
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Receipt(Item item, String purchaserIntraId, String ownerIntraId,
		ItemStatus status, LocalDateTime purchasedAt) {
		this.item = item;
		this.purchaserIntraId = purchaserIntraId;
		this.ownerIntraId = ownerIntraId;
		this.status = status;
		this.createdAt = purchasedAt;
	}

	@Override
	public String toString() {
		return "Receipt{"
			+ "id=" + id
			+ ", item=" + item
			+ ", purchaserIntraId='" + purchaserIntraId + '\''
			+ ", ownerIntraId='" + ownerIntraId + '\''
			+ ", status=" + status
			+ ", purchasedAt=" + createdAt
			+ '}';
	}

	public void updateStatus(ItemStatus status) {
		this.status = status;
	}
}
