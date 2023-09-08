package com.gg.server.domain.item.service;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.data.UserItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import com.gg.server.domain.item.dto.UserItemListResponseDto;
import com.gg.server.domain.item.dto.UserItemResponseDto;
import com.gg.server.domain.item.exception.*;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
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

    @Transactional(readOnly = true)
    public ItemStoreListResponseDto getAllItems() {

        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByCreatedAtDesc()
                .stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
        return new ItemStoreListResponseDto(itemStoreListResponseDto);
    }

    @Transactional
    public void purchaseItem(Long itemId, UserDto userDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow( ()->  {
                    throw new ItemNotFoundException();
                });
        if (!item.getIsVisible())
        {
            throw new ItemNotPurchasableException();
        }

        //세일가격 존재할때 세일가로 결정
        Integer finalPrice;
        if (item.getDiscount() != null && item.getDiscount() > 0) {
            finalPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
        }
        else {
            finalPrice = item.getPrice();
        }

        // 사용자의 GGcoin이 상품 가격보다 낮으면 예외 처리.
        if (userDto.getGgCoin() < finalPrice) {
            throw new InsufficientGgcoinException();
        }

        User payUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException());

        if (payUser.getRoleType() == RoleType.GUEST) {
            throw new KakaoPurchaseException();
        }

        payUser.payGgCoin(finalPrice);       //상품 구매에 따른 차감

        Receipt receipt = new Receipt(item, userDto.getIntraId(), userDto.getIntraId(),
                ItemStatus.BEFORE, LocalDateTime.now());
        receiptRepository.save(receipt);
    }

    @Transactional
    public void giftItem(Long itemId, String ownerId, UserDto userDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow( ()->  {
                    throw new ItemNotFoundException();
                });
        if (!item.getIsVisible()) {
            throw new ItemNotPurchasableException();
        }

        //세일가격 존재할때 세일가로 결정
        Integer finalPrice;
        if (item.getDiscount() != null && item.getDiscount() > 0) {
            finalPrice = item.getPrice() - (item.getPrice() * item.getDiscount() / 100);
        }
        else {
            finalPrice = item.getPrice();
        }

        // 사용자의 GGcoin이 상품 가격보다 낮으면 예외 처리.
        if (userDto.getGgCoin() < finalPrice) {
            throw new InsufficientGgcoinException();
        }

        User payUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException());

        if (payUser.getRoleType() == RoleType.GUEST) {
            throw new KakaoPurchaseException();
        }

        User owner = userRepository.findByIntraId(ownerId)
                .orElseThrow(() -> new UserNotFoundException());

        if (owner.getRoleType() == RoleType.GUEST) {
            throw new KakaoGiftException();
        }

        payUser.payGgCoin(finalPrice);       //상품 구매에 따른 차감

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
}
