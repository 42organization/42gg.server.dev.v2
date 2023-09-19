package com.gg.server.domain.item.service;

import com.gg.server.domain.coin.service.UserCoinChangeService;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.data.UserItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import com.gg.server.domain.item.dto.UserItemListResponseDto;
import com.gg.server.domain.item.dto.UserItemResponseDto;
import com.gg.server.domain.item.exception.*;
import com.gg.server.domain.item.type.ItemType;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ItemStatusException;
import com.gg.server.domain.receipt.exception.ReceiptNotOwnerException;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ReceiptRepository receiptRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;
    private final NotiService notiService;
    private final UserCoinChangeService userCoinChangeService;

    @Transactional(readOnly = true)
    public ItemStoreListResponseDto getAllItems() {

        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByCreatedAtDesc()
                .stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
        return new ItemStoreListResponseDto(itemStoreListResponseDto);
    }

    @Transactional
    public void purchaseItem(Long itemId, UserDto userDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException();
                });
        if (!item.getIsVisible()) {
            throw new ItemNotPurchasableException();
        }

        //세일가격 존재할때 세일가로 결정
        Integer finalPrice;
        if (item.getDiscount() != null && item.getDiscount() > 0) {
            finalPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
        } else {
            finalPrice = item.getPrice();
        }

        userCoinChangeService.purchaseItemCoin(item, finalPrice, userDto.getId());

        Receipt receipt = new Receipt(item, userDto.getIntraId(), userDto.getIntraId(),
                ItemStatus.BEFORE, LocalDateTime.now());
        receiptRepository.save(receipt);
    }

    @Transactional
    public void giftItem(Long itemId, String ownerId, UserDto userDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException();
                });
        if (!item.getIsVisible()) {
            throw new ItemNotPurchasableException();
        }

        //세일가격 존재할때 세일가로 결정
        Integer finalPrice;
        if (item.getDiscount() != null && item.getDiscount() > 0) {
            finalPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
        } else {
            finalPrice = item.getPrice();
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

    @Transactional(readOnly = true)
    public UserItemListResponseDto getItemByUser(UserDto userDto, Pageable pageable) {
        Page<Receipt> receipts = userItemRepository.findByOwnerIntraId(userDto.getIntraId(), pageable);
        Page<UserItemResponseDto> responseDtos = receipts.map(UserItemResponseDto::new);
        return new UserItemListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }

    public void checkItemOwner(User loginUser, Receipt receipt) {
        if (!receipt.getOwnerIntraId().equals(loginUser.getIntraId()))
            throw new ReceiptNotOwnerException();
    }

    public void checkItemType(Receipt receipt, ItemType itemType) {
        if (!receipt.getItem().getType().equals(itemType))
            throw new ItemTypeException();
    }

    public void checkItemStatus(Receipt receipt) {
        if (receipt.getItem().getType().equals(ItemType.MEGAPHONE)) {
            if (!(receipt.getStatus().equals(ItemStatus.WAITING) || receipt.getStatus().equals(ItemStatus.USING))) throw new ItemStatusException();
        } else {
            if (!receipt.getStatus().equals(ItemStatus.BEFORE)) throw new ItemStatusException();
        }
    }
}
