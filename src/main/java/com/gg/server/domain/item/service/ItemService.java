package com.gg.server.domain.item.service;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import com.gg.server.domain.item.exception.InsufficientGgcoinException;
import com.gg.server.domain.item.exception.ItemNotFoundException;
import com.gg.server.domain.item.exception.ItemNotPurchasableException;
import com.gg.server.domain.item.exception.KakaoPurchaseException;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RoleType;
import lombok.RequiredArgsConstructor;
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

    public ItemStoreListResponseDto getAllItems() {
        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByIsVisible(true)
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

}
