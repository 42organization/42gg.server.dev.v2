package com.gg.server.domain.receipt.data;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.receipt.type.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    @Column(name = "created_At")
    private LocalDateTime purchasedAt;

    public Receipt(Item item, String purchaserIntraId, String ownerIntraId,
                   ItemStatus status, LocalDateTime purchasedAt){
        this.item = item;
        this.purchaserIntraId = purchaserIntraId;
        this.ownerIntraId = ownerIntraId;
        this.status = status;
        this.purchasedAt = purchasedAt;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", item=" + item +
                ", purchaserIntraId='" + purchaserIntraId + '\'' +
                ", ownerIntraId='" + ownerIntraId + '\'' +
                ", status=" + status +
                ", purchasedAt=" + purchasedAt +
                '}';
    }

    public void updateStatus() {
        if (status == ItemStatus.BEFORE) {
            this.status = ItemStatus.USING;
        } else {
            this.status = ItemStatus.USED;
        }
    }
}