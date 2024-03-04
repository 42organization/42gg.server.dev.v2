package gg.pingpong.api.admin.store.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.admin.repo.store.ItemAdminRepository;
import gg.data.store.Item;
import gg.pingpong.api.admin.store.controller.request.ItemUpdateRequestDto;
import gg.pingpong.api.admin.store.controller.response.ItemHistoryResponseDto;
import gg.pingpong.api.admin.store.controller.response.ItemListResponseDto;
import gg.pingpong.api.global.utils.aws.AsyncNewItemImageUploader;
import gg.auth.UserDto;
import gg.utils.exception.item.ItemNotAvailableException;
import gg.utils.exception.item.ItemNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemAdminService {

	private final ItemAdminRepository itemAdminRepository;
	private final AsyncNewItemImageUploader asyncNewItemImageUploader;

	/**
	 * <p>Item 히스토리를 반환한다.</p>
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public ItemListResponseDto getAllItemHistory(Pageable pageable) {
		Page<ItemHistoryResponseDto> responseDtos = itemAdminRepository.findAll(pageable)
			.map(ItemHistoryResponseDto::new);
		return new ItemListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
	}

	/**
	 * <p>아이템 수정 시 신규 이미지가 존재하는 경우</p>
	 * <p>기존 아이템의 item.isVisible 를 false 로 변경한다</p>
	 * @param itemId 타겟 아이템
	 * @param itemUpdateRequestDto 타겟 아이템 변경 dto
	 * @param itemImageFile 신규 이미지
	 * @param user 바꾸는 유저 id
	 * @throws IOException IOException
	 * @throws ItemNotFoundException 아이템 없음
	 * @throws ItemNotAvailableException 접근 불가한 아이템
	 */
	@Transactional
	public void updateItem(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto,
		MultipartFile itemImageFile, UserDto user) throws IOException {
		Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
		if (!item.getIsVisible()) {
			throw new ItemNotAvailableException();
		}
		item.setVisibility(user.getIntraId());
		Item newItem = itemUpdateRequestDto.toItem(null, user.getIntraId());
		if (itemImageFile != null) {
			asyncNewItemImageUploader.upload(newItem, itemImageFile);
		}
		itemAdminRepository.save(newItem);
	}

	/**
	 * <p>아이템 수정 시 신규 이미지가 존재하지 않는 경우</p>
	 * <p>기존 아이템의 item.isVisible 를 false 로 변경한다</p>
	 * @param itemId 타겟 아이템
	 * @param itemUpdateRequestDto 타겟 아이템 변경 dto
	 * @param user 바꾸는 유저 id
	 * @throws ItemNotFoundException 아이템 없음
	 * @throws ItemNotAvailableException 접근 불가한 아이템
	 */
	@Transactional
	public void updateItem(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto, UserDto user) {
		Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
		if (!item.getIsVisible()) {
			throw new ItemNotAvailableException();
		}
		item.setVisibility(user.getIntraId());
		Item newItem = itemUpdateRequestDto.toItem(item.getImageUri(), user.getIntraId());
		itemAdminRepository.save(newItem);
	}

	/**
	 * <p>아이템 삭제</p>
	 * <p>item.isVisible 를 false 로 변경한다</p>
	 * @param itemId 타겟 id
	 * @param user 삭제하는 유저
	 * @throws ItemNotFoundException 아이템 없음
	 */
	@Transactional
	public void deleteItem(Long itemId, UserDto user) {
		Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
		item.setVisibility(user.getIntraId());
	}
}
