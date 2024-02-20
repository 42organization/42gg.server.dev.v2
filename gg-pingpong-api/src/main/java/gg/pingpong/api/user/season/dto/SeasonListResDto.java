package gg.pingpong.api.user.season.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeasonListResDto {
	List<SeasonResDto> seasonList;

	public SeasonListResDto(List<SeasonResDto> seasonList) {
		this.seasonList = seasonList;
	}
}
