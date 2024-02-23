package gg.pingpong.api.admin.manage.controller.response;

import java.util.List;

import gg.pingpong.api.admin.manage.dto.SlotAdminDto;
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
