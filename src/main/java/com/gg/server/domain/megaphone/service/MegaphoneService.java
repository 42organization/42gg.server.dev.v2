package com.gg.server.domain.megaphone.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.item.type.ItemType;
import com.gg.server.domain.megaphone.data.Megaphone;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.megaphone.dto.MegaphoneDetailResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneTodayListResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.exception.MegaphoneContentException;
import com.gg.server.domain.megaphone.exception.MegaphoneNotFoundException;
import com.gg.server.domain.megaphone.exception.MegaphoneTimeException;
import com.gg.server.domain.megaphone.redis.MegaphoneRedis;
import com.gg.server.domain.megaphone.redis.MegaphoneRedisRepository;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ItemStatusException;
import com.gg.server.domain.receipt.exception.ReceiptNotFoundException;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RoleType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MegaphoneService {
	private final UserRepository userRepository;
	private final ReceiptRepository receiptRepository;
	private final MegaphoneRepository megaphoneRepository;
	private final MegaphoneRedisRepository megaphoneRedisRepository;
	private final ItemService itemService;

	@Transactional
	public void useMegaphone(MegaphoneUseRequestDto megaphoneUseRequestDto, UserDto user) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		if (LocalTime.now().isAfter(LocalTime.of(23, 55)) || LocalTime.now().isBefore(LocalTime.of(0, 5))) {
			throw new MegaphoneTimeException();
		}
		Receipt receipt = receiptRepository.findById(megaphoneUseRequestDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);
		itemService.checkItemType(receipt, ItemType.MEGAPHONE);
		itemService.checkItemOwner(loginUser, receipt);
		if (!receipt.getStatus().equals(ItemStatus.BEFORE)) {
			throw new ItemStatusException();
		}
		if (megaphoneUseRequestDto.getContent().length() == 0) {
			throw new MegaphoneContentException();
		}
		receipt.updateStatus(ItemStatus.WAITING);
		Megaphone megaphone = new Megaphone(loginUser, receipt, megaphoneUseRequestDto.getContent(),
			LocalDate.now().plusDays(1));
		megaphoneRepository.save(megaphone);
	}

	@Transactional
	public void setMegaphoneList(LocalDate today) {
		megaphoneRepository.findAllByUsedAtAndReceiptStatus(today, ItemStatus.USING)
			.forEach(megaphone -> megaphone.getReceipt().updateStatus(ItemStatus.USED));
		megaphoneRedisRepository.deleteAllMegaphone();
		List<Megaphone> megaphones = megaphoneRepository.findAllByUsedAtAndReceiptStatus(today.plusDays(1),
			ItemStatus.WAITING);
		for (Megaphone megaphone : megaphones) {
			megaphone.getReceipt().updateStatus(ItemStatus.USING);
			megaphoneRedisRepository.addMegaphone(
				new MegaphoneRedis(megaphone.getId(), megaphone.getUser().getIntraId(), megaphone.getContent(),
					LocalDateTime.of(megaphone.getUsedAt(), LocalTime.of(0, 0))));
		}
	}

	@Transactional
	public void deleteMegaphone(Long megaphoneId, UserDto user) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		Megaphone megaphone = megaphoneRepository.findById(megaphoneId).orElseThrow(MegaphoneNotFoundException::new);
		Receipt receipt = megaphone.getReceipt();
		if (!user.getRoleType().equals(RoleType.ADMIN)) {
			itemService.checkItemOwner(loginUser, receipt);
		}
		itemService.checkItemStatus(receipt);
		if (receipt.getStatus().equals(ItemStatus.USING)) {
			megaphoneRedisRepository.deleteMegaphoneById(megaphone.getId());
		}
		receipt.updateStatus(ItemStatus.DELETED);
	}

	public MegaphoneDetailResponseDto getMegaphoneDetail(Long receiptId, UserDto user) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(ReceiptNotFoundException::new);
		itemService.checkItemType(receipt, ItemType.MEGAPHONE);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);
		Megaphone megaphone = megaphoneRepository.findByReceipt(receipt).orElseThrow(MegaphoneNotFoundException::new);
		return new MegaphoneDetailResponseDto(megaphone);
	}

	public List<MegaphoneTodayListResponseDto> getMegaphoneTodayList() {
		return megaphoneRedisRepository.getAllMegaphone()
			.stream()
			.map(MegaphoneTodayListResponseDto::new)
			.collect(Collectors.toList());
	}
}
