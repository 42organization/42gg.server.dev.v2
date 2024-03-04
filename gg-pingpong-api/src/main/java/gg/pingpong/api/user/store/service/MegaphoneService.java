package gg.pingpong.api.user.store.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.store.Megaphone;
import gg.data.store.Receipt;
import gg.data.store.redis.MegaphoneRedis;
import gg.data.store.type.ItemStatus;
import gg.data.store.type.ItemType;
import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.pingpong.api.user.store.controller.request.MegaphoneUseRequestDto;
import gg.pingpong.api.user.store.controller.response.MegaphoneDetailResponseDto;
import gg.pingpong.api.user.store.controller.response.MegaphoneTodayListResponseDto;
import gg.pingpong.api.user.store.redis.MegaphoneRedisRepository;
import gg.auth.UserDto;
import gg.repo.store.MegaphoneRepository;
import gg.repo.store.ReceiptRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.item.ItemTypeException;
import gg.utils.exception.megaphone.MegaphoneContentException;
import gg.utils.exception.megaphone.MegaphoneNotFoundException;
import gg.utils.exception.megaphone.MegaphoneTimeException;
import gg.utils.exception.receipt.ItemStatusException;
import gg.utils.exception.receipt.ReceiptNotFoundException;
import gg.utils.exception.receipt.ReceiptNotOwnerException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MegaphoneService {
	private final UserRepository userRepository;
	private final ReceiptRepository receiptRepository;
	private final MegaphoneRepository megaphoneRepository;
	private final MegaphoneRedisRepository megaphoneRedisRepository;
	private final ItemService itemService;

	/**
	 * <p>메가폰을 사용합니다.</p>
	 * <p>00:06 ~ 23:54 분 사이에 사용 가능 합니다.</p>
	 * @param megaphoneUseRequestDto 요청 dto
	 * @param user 접속 유저
	 * @param localTime 현재 날짜
	 * @throws UserNotFoundException 유저 없을 때
	 * @throws MegaphoneTimeException 메가폰 사용 가능 기간 아닐때
	 * @throws ReceiptNotFoundException 영수증 없을 때
	 * @throws ItemTypeException 메가폰 아닐 때
	 * @throws ReceiptNotOwnerException 영수증에 적힌 사용자가 아닐 때
	 * @throws ItemStatusException 사용전인 아이템이 아닐때
	 * @throws MegaphoneContentException 메가폰 내용이 없을 때

	 */
	@Transactional
	public void useMegaphone(MegaphoneUseRequestDto megaphoneUseRequestDto, UserDto user, LocalTime localTime) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		if (localTime.isAfter(LocalTime.of(23, 55)) || localTime.isBefore(LocalTime.of(0, 5))) {
			throw new MegaphoneTimeException();
		}
		Receipt receipt = receiptRepository.findById(megaphoneUseRequestDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);
		itemService.checkItemType(receipt, ItemType.MEGAPHONE);
		itemService.checkItemOwner(loginUser, receipt);
		if (!receipt.getStatus().equals(ItemStatus.BEFORE)) {
			throw new ItemStatusException();
		}
		if (megaphoneUseRequestDto.getContent().isEmpty()) {
			throw new MegaphoneContentException();
		}
		receipt.updateStatus(ItemStatus.WAITING);
		Megaphone megaphone = new Megaphone(loginUser, receipt, megaphoneUseRequestDto.getContent(),
			LocalDate.now().plusDays(1));
		megaphoneRepository.save(megaphone);
	}

	/**
	 * <p>현재 사용중인 메가폰은 삭제하고, 다음날 메가폰들을 등록 시켜준다.</p>
	 * @param today 현재 날짜.
	 */
	@Transactional
	public void setMegaphoneList(LocalDate today) {
		megaphoneRepository.findAllByUsedAtAndReceiptStatus(today, ItemStatus.USING)
			.forEach(megaphone -> megaphone.getReceipt().updateStatus(ItemStatus.USED));
		megaphoneRedisRepository.deleteAllMegaphone();
		List<Megaphone> megaphones = megaphoneRepository
			.findAllByUsedAtAndReceiptStatus(today.plusDays(1), ItemStatus.WAITING);
		for (Megaphone megaphone : megaphones) {
			megaphone.getReceipt().updateStatus(ItemStatus.USING);
			megaphoneRedisRepository.addMegaphone(new MegaphoneRedis(megaphone.getId(),
				megaphone.getUser().getIntraId(), megaphone.getContent(),
				LocalDateTime.of(megaphone.getUsedAt(), LocalTime.of(0, 0))));
		}
	}

	/**
	 * <p>메가폰을 삭제합니다. 관리자와 일반유저가 같이 사용하는 메서드입니다.</p>
	 * @param megaphoneId 타겟 id
	 * @param user 접속 유저
	 * @throws UserNotFoundException 유저가 없을 때
	 * @throws MegaphoneNotFoundException 타겟 메가폰 없을 때
	 * @throws ReceiptNotOwnerException 주인이 아닐 때
	 * @throws ItemStatusException 아이템 상태 에러
	 */
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

	/**
	 * <p>메가폰 세부사항을 반환해줍니다.</p>
	 * @param receiptId 타겟 id
	 * @param user 접속 유저
	 * @throws UserNotFoundException 유저 없을 때
	 * @throws ReceiptNotFoundException 영수증이 없을 때
	 * @throws ItemTypeException 메가폰이 아닐 때
	 * @throws ReceiptNotOwnerException 주인이 아닐때
	 * @throws ItemStatusException 아이템의 상태 에러
	 * @throws MegaphoneNotFoundException 없는 메가폰 일때
	 */
	public MegaphoneDetailResponseDto getMegaphoneDetail(Long receiptId, UserDto user) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(ReceiptNotFoundException::new);
		itemService.checkItemType(receipt, ItemType.MEGAPHONE);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);
		Megaphone megaphone = megaphoneRepository.findByReceipt(receipt).orElseThrow(MegaphoneNotFoundException::new);
		return new MegaphoneDetailResponseDto(megaphone);
	}

	/**
	 * <p>오늘 띄워지는 메가폰들을 가져옵니다.</p>
	 * @return
	 */
	public List<MegaphoneTodayListResponseDto> getMegaphoneTodayList() {
		return megaphoneRedisRepository.getAllMegaphone().stream()
			.map(MegaphoneTodayListResponseDto::new).collect(Collectors.toList());
	}
}
