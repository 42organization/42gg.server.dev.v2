package com.gg.server.admin.slotmanagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.slotmanagement.data.AdminSlotManagementsRepository;
import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.exception.SlotManagementForbiddenException;
import com.gg.server.domain.slotmanagement.exception.SlotManagementNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotAdminService {
	private final AdminSlotManagementsRepository adminSlotManagementRepository;

	/**
	 * <p>현재 슬롯 정보를 가져온다.</p>
	 * @return
	 */
	@Transactional(readOnly = true)
	public SlotListAdminResponseDto getSlotSetting() {
		List<SlotManagement> slotManagements = adminSlotManagementRepository.findAfterNowSlotManagement(
			LocalDateTime.now());
		List<SlotAdminDto> dtoList = new ArrayList<>();
		for (SlotManagement slot : slotManagements) {
			SlotAdminDto dto = new SlotAdminDto(slot);
			dtoList.add(dto);
		}
		return new SlotListAdminResponseDto(dtoList);
	}

	/**
	 * <p>새로운 슬롯을 추가한다.</p>
	 * @param requestDto
	 */
	@Transactional
	public void addSlotSetting(SlotCreateRequestDto requestDto) {
		checkVaildSlotManagement(requestDto);
		requestDto.updateStartTime();
		updateNowSlotManagementEndTime(requestDto.getStartTime());
		SlotManagement slotManagement = new SlotManagement(requestDto);

		adminSlotManagementRepository.save(slotManagement);
	}

	/**
	 * <p>현재 시간보다 이후 혹은 종료 시간이 이후인 슬롯 정보를 삭제한다.</p>
	 */
	@Transactional
	public void delSlotSetting() {
		List<SlotManagement> slotManagements = adminSlotManagementRepository.findAfterNowSlotManagement(
			LocalDateTime.now());

		SlotManagement slotManagement = slotManagements.get(0);
		if (LocalDateTime.now().isAfter(slotManagement.getStartTime())) {
			throw new SlotManagementForbiddenException();
		}
		adminSlotManagementRepository.delete(slotManagement);

		SlotManagement beforeSlotManagement = slotManagements.get(1);
		beforeSlotManagement.setNullEndTime();
	}

	/**
	 * <p>서비스 로직 조건에 맞는지 체크</p>
	 * @param requestDto
	 */
	private void checkVaildSlotManagement(SlotCreateRequestDto requestDto) {
		if (requestDto.getPastSlotTime() > 23) {
			throw new SlotManagementForbiddenException();
		}
		if (requestDto.getFutureSlotTime() > 12) {
			throw new SlotManagementForbiddenException();
		}
		if (60 % requestDto.getInterval() != 0 || requestDto.getInterval() % 5 != 0) {
			throw new SlotManagementForbiddenException();
		}
		if (requestDto.getPastSlotTime() > requestDto.getInterval()) {
			throw new SlotManagementForbiddenException();
		}
	}

	/**
	 * <p>서비스 로직 조건에 맞는지 체크</p>
	 * @param endTime
	 */
	private void updateNowSlotManagementEndTime(LocalDateTime endTime) {
		SlotManagement nowSlotManagement = adminSlotManagementRepository.findFirstByOrderByIdDesc()
			.orElseThrow(SlotManagementNotFoundException::new);

		LocalDateTime nowFutureSlotTime = LocalDateTime.now().isAfter(nowSlotManagement.getStartTime())
			? LocalDateTime.now().plusHours(nowSlotManagement.getFutureSlotTime())
			: nowSlotManagement.getStartTime().plusHours(nowSlotManagement.getFutureSlotTime());

		if (nowFutureSlotTime.isAfter(endTime)) {
			throw new SlotManagementForbiddenException();
		}

		nowSlotManagement.updateEndTime(endTime);
	}
}
