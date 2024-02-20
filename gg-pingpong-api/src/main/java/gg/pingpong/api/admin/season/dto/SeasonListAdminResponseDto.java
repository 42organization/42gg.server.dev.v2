package gg.pingpong.api.admin.season.dto;

import java.util.List;

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
