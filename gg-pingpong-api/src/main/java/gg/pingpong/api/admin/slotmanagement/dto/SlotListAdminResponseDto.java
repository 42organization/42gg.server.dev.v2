package gg.pingpong.api.admin.slotmanagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SlotListAdminResponseDto {
	List<SlotAdminDto> slotList;

	public SlotListAdminResponseDto(List<SlotAdminDto> slotList) {
		this.slotList = slotList;
	}
}
