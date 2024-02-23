package gg.pingpong.api.user.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.store.controller.response.ItemStoreListResponseDto;
import gg.pingpong.api.user.store.controller.response.ItemStoreResponseDto;
import gg.pingpong.api.user.store.controller.response.UserItemListResponseDto;
import gg.pingpong.api.user.store.controller.response.UserItemResponseDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.store.Item;
import gg.pingpong.data.store.Receipt;
import gg.pingpong.data.store.type.ItemStatus;
import gg.pingpong.data.store.type.ItemType;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.repo.item.ItemRepository;
import gg.pingpong.repo.item.UserItemRepository;
import gg.pingpong.repo.receipt.ReceiptRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.exception.item.ItemNotFoundException;
import gg.pingpong.utils.exception.item.ItemNotPurchasableException;
import gg.pingpong.utils.exception.item.ItemTypeException;
import gg.pingpong.utils.exception.item.KakaoGiftException;
import gg.pingpong.utils.exception.item.KakaoPurchaseException;
import gg.pingpong.utils.exception.receipt.ItemStatusException;
import gg.pingpong.utils.exception.receipt.ReceiptNotOwnerException;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ReceiptRepository receiptRepository;
	private final UserRepository userRepository;
	private final UserItemRepository userItemRepository;
	private final NotiService notiService;
	private final UserCoinChangeService userCoinChangeService;

	/**
	 * <p>모든 아이템을 가져온다.</p>
	 * @return
	 */
	@Transactional(readOnly = true)
	public ItemStoreListResponseDto getAllItems() {
		List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByCreatedAtDesc()
			.stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
		return new ItemStoreListResponseDto(itemStoreListResponseDto);
	}

	/**
	 * <p>게스트 유저가 아닌 유저가 아이템을 구매하는 메서드 이다</p>
	 * <p>할인 중 이라면 할인가를 적용하고, 구매 후 영수증을 db에 저장한다.</p>
	 * @param itemId 타겟 아이템 Id
	 * @param userDto 구매 유저 정보
	 * @throws ItemNotFoundException 존재하지 않는 아이템
	 * @throws ItemNotPurchasableException 구매할 수 없는 아이템
	 * @throws UserNotFoundException 타겟 유저 없음
	 * @throws KakaoPurchaseException 게스트(카카오) 유저가 아이템을 구매하려 할때
	 * @throws
	 */
	@Transactional
	public void purchaseItem(Long itemId, UserDto userDto) {
		Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
		if (!item.getIsVisible()) {
			throw new ItemNotPurchasableException();
		}

		//세일가격 존재할때 세일가로 결정
		Integer finalPrice = item.getPrice();
		if (item.getDiscount() != null && item.getDiscount() > 0) {
			finalPrice -= (item.getPrice() * item.getDiscount() / 100);
		}

		User payUser = userRepository.findById(userDto.getId())
			.orElseThrow(UserNotFoundException::new);

		if (payUser.getRoleType() == RoleType.GUEST) {
			throw new KakaoPurchaseException();
		}

		userCoinChangeService.purchaseItemCoin(item, finalPrice, userDto.getId());

		Receipt receipt = new Receipt(item, userDto.getIntraId(), userDto.getIntraId(),
			ItemStatus.BEFORE, LocalDateTime.now());
		receiptRepository.save(receipt);
	}

	/**
	 * <p>게스트 유저가 아닌 유저들 끼리 선물을 주는 메서드 이다</p>
	 * <p>할인 중 이라면 할인가를 적용하고, 구매 후 영수증을 db에 저장한다.</p>
	 * @param itemId 타겟 아이템 id
	 * @param ownerId 선물 받는 owner intraId
	 * @param userDto 구매자 id
	 * @throws ItemNotFoundException 존재하지 않는 아이템
	 * @throws ItemNotPurchasableException 구매할 수 없는 아이템
	 * @throws UserNotFoundException 타겟 유저 없음
	 * @throws KakaoPurchaseException 게스트(카카오) 유저가 아이템을 구매하려 할때
	 * @throws KakaoGiftException 게스트(카카오) 유저에게 선물하려 할때
	 */
	@Transactional
	public void giftItem(Long itemId, String ownerId, UserDto userDto) {
		Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
		if (!item.getIsVisible()) {
			throw new ItemNotPurchasableException();
		}

		//세일가격 존재할때 세일가로 결정
		Integer finalPrice = item.getPrice();
		if (item.getDiscount() != null && item.getDiscount() > 0) {
			finalPrice -= (item.getPrice() * item.getDiscount() / 100);
		}

		User payUser = userRepository.findById(userDto.getId())
			.orElseThrow(UserNotFoundException::new);

		if (payUser.getRoleType() == RoleType.GUEST) {
			throw new KakaoPurchaseException();
		}

		User owner = userRepository.findByIntraId(ownerId)
			.orElseThrow(UserNotFoundException::new);

		if (owner.getRoleType() == RoleType.GUEST) {
			throw new KakaoGiftException();
		}

		userCoinChangeService.giftItemCoin(item, finalPrice, payUser, owner);

		Receipt receipt = new Receipt(item, userDto.getIntraId(), ownerId,
			ItemStatus.BEFORE, LocalDateTime.now());
		receiptRepository.save(receipt);
		notiService.createGiftNoti(owner, payUser, item.getName());
	}

	/**
	 * <p>유저가 구매한 아이템 중 상태가 BEFORE, USING, WAITING 인 것들을 찾는 메서드이다.</p>
	 * @param userDto 유저 정보
	 * @param pageable 페이지
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserItemListResponseDto getItemByUser(UserDto userDto, Pageable pageable) {
		Page<Receipt> receipts = userItemRepository.findByOwnerIntraId(userDto.getIntraId(), pageable);
		Page<UserItemResponseDto> responseDto = receipts.map(UserItemResponseDto::new);
		return new UserItemListResponseDto(responseDto.getContent(), responseDto.getTotalPages());
	}

	/**
	 * <p>해당 아이템의 주인이 맞는지 체크한다.</p>
	 * @param loginUser 로그인 유저
	 * @param receipt 영수증
	 */
	public void checkItemOwner(User loginUser, Receipt receipt) {
		if (!receipt.getOwnerIntraId().equals(loginUser.getIntraId())) {
			throw new ReceiptNotOwnerException();
		}
	}

	/**
	 * <p>해당 아이템의 타입이 영수증과 맞는지 체크한다.</p>
	 * @param receipt 영수증
	 * @param itemType 아이템 타입
	 */
	public void checkItemType(Receipt receipt, ItemType itemType) {
		if (!receipt.getItem().getType().equals(itemType)) {
			throw new ItemTypeException();
		}
	}

	/**
	 * <p>아이템의 상태를 체크한다.</p>
	 * <p>메가폰인데 ItemStatus.WAITING 가 아니거나 사용중이라면 예외 발생</p>
	 * <p>메가폰이 아닌 아이템인데 ItemStatus.BEFORE 인경우 예외 발생</p>
	 * @param receipt
	 */
	public void checkItemStatus(Receipt receipt) {
		if (receipt.getItem().getType().equals(ItemType.MEGAPHONE)) {
			if (!(receipt.getStatus().equals(ItemStatus.WAITING)
				|| receipt.getStatus().equals(ItemStatus.USING))) {
				throw new ItemStatusException();
			}
		} else if (!receipt.getStatus().equals(ItemStatus.BEFORE)) {
			throw new ItemStatusException();
		}
	}
}
