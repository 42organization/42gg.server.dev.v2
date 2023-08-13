package com.gg.server.domain.item.service;

import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import com.gg.server.domain.item.dto.PurchaseItemRequestDto;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
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

    public ItemStoreListResponseDto getAllItems() {
        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByIsVisible(true)
                .stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
        return new ItemStoreListResponseDto(itemStoreListResponseDto);
    }

    @Transactional
    public void purchaseItem(Long itemId, UserDto userDto, PurchaseItemRequestDto requestDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow( ()-> new IllegalArgumentException("해당 아이템이 없습니다" + itemId) );
        if (!item.getIsVisible())
        {
            throw new IllegalArgumentException("지금은 구매할 수 없는 아이템 입니다.");
        }

        Receipt receipt = new Receipt(item, userDto.getIntraId(), userDto.getIntraId(),
                ItemStatus.BEFORE, LocalDateTime.now());
        receiptRepository.save(receipt);
    }
}
//
//    private final ItemRepository itemRepository;
//    private final ReceiptRepository receiptRepository;
//
//    public ItemStoreListResponseDto getAllItems() {
//        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByIsVisible(true)
//                .stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
//        return new ItemStoreListResponseDto(itemStoreListResponseDto);
//    }
//
//    @Transactional
//    public void purchaseItem(Long itemId, UserDto userDto, PurchaseItemRequestDto requestDto) {
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new IllegalArgumentException("No item found with id: " + itemId));
//
//        if(!item.getIsVisible()) {
//            throw new IllegalArgumentException("Item is not available for purchase");
//        }
//
//        Receipt receipt = new Receipt(item, userDto.getIntraId(), userDto.getIntraId(), ItemStatus.BEFORE, LocalDateTime.now());
//        receiptRepository.save(receipt);
//    }