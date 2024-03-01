package gg.pingpong.api.admin.season.controller.response;

import java.util.List;

import gg.pingpong.api.admin.season.dto.SeasonAdminDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeasonListAdminResponseDto {
	List<SeasonAdminDto> seasonList;

	public SeasonListAdminResponseDto(List<SeasonAdminDto> seasonList) {
		this.seasonList = seasonList;
	}

}
