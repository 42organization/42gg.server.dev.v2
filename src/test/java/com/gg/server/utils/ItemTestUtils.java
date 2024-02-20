package com.gg.server.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.data.store.Item;
import com.gg.server.data.store.Megaphone;
import com.gg.server.data.store.Receipt;
import com.gg.server.data.store.type.ItemStatus;
import com.gg.server.data.user.User;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.receipt.data.ReceiptRepository;

import lombok.AllArgsConstructor;

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
		Item item = updateRequestDto.toItem(creator.getIntraId(), "42gg-s3");
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
