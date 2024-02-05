package com.gg.server.domain.slotmanagement;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.domain.slotmanagement.exception.SlotManagementForbiddenException;
import com.gg.server.global.utils.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SlotManagement extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "past_slot_time")
	private Integer pastSlotTime;

	@NotNull
	@Column(name = "future_slot_time")
	private Integer futureSlotTime;

	@NotNull
	@Column(name = "open_minute")
	private Integer openMinute;

	@NotNull
	@Column(name = "game_interval")
	private Integer gameInterval;

	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Builder
	public SlotManagement(Integer pastSlotTime, Integer futureSlotTime, Integer openMinute, Integer gameInterval,
		LocalDateTime startTime, LocalDateTime endTime) {
		this.pastSlotTime = pastSlotTime;
		this.futureSlotTime = futureSlotTime;
		this.openMinute = openMinute;
		this.gameInterval = gameInterval;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Builder
	public SlotManagement(SlotCreateRequestDto requestDto) {
		this.pastSlotTime = requestDto.getPastSlotTime();
		this.futureSlotTime = requestDto.getFutureSlotTime();
		this.openMinute = requestDto.getOpenMinute();
		this.gameInterval = requestDto.getInterval();
		this.startTime = requestDto.getStartTime();
		this.endTime = null;
	}

	public void updateEndTime(LocalDateTime endTime) {
		if (this.endTime != null) {
			throw new SlotManagementForbiddenException();
		}
		this.endTime = endTime;
	}

	public void setNullEndTime() {
		if (this.endTime == null) {
			throw new SlotManagementForbiddenException();
		}
		this.endTime = null;
	}
}
