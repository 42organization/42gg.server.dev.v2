package gg.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import gg.data.pingpong.store.Item;
import gg.data.pingpong.store.Megaphone;
import gg.data.pingpong.store.Receipt;
import gg.data.pingpong.store.type.ItemStatus;
import gg.data.user.User;
import gg.pingpong.api.admin.store.controller.request.ItemUpdateRequestDto;
import gg.repo.store.ItemRepository;
import gg.repo.store.MegaphoneRepository;
import gg.repo.store.ReceiptRepository;
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
		Item item = updateRequestDto.toItem("42gg-s3", creator.getIntraId());
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
