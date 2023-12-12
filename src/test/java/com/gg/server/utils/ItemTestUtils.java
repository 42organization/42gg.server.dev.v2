package com.gg.server.utils;

import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.megaphone.data.Megaphone;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ItemTestUtils.
 *
 * <p>
 *
 * </p>
 *
 * @author : middlefitting
 * @since : 2023/12/08
 */
@Component
@AllArgsConstructor
public class ItemTestUtils {

  ItemRepository itemRepository;

  ReceiptRepository receiptRepository;

  MegaphoneRepository megaphoneRepository;

  /**
   * 아이템을 구매한다.(영수증 생성)
   */
  public Receipt purchaseItem(User purchaser, User owner, Item item) {
    Receipt receipt = new Receipt(item, purchaser.getIntraId(), owner.getIntraId(),
        ItemStatus.BEFORE, LocalDateTime.now());
    return receiptRepository.save(receipt);
  }

  /**
   * 아이템을 생성한다.
   */
  public Item createItem(User creator, ItemUpdateRequestDto updateRequestDto) {
    Item item = Item.builder()
        .creatorIntraId(creator.getIntraId())
        .itemImageUri("42gg-s3")
        .updateRequestDto(updateRequestDto)
        .build();
    itemRepository.save(item);
    return item;
  }

  /**
   * 메가폰을 생성한다.
   * 현재 서비스에 맞게 WAITING 상태로 변경한다.
   */
  public Megaphone createMegaPhone(User user, Receipt receipt, String content) {
    Megaphone mega = new Megaphone(user, receipt, content, LocalDate.now().plusDays(1));
    receipt.updateStatus(ItemStatus.WAITING);
    receiptRepository.save(receipt);
    megaphoneRepository.save(mega);
    return mega;
  }
}
