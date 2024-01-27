package com.gg.server.domain.season.dto;

import com.gg.server.domain.season.data.Season;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonResDto {
	private Long id;
	private String name;

	public SeasonResDto(Season season) {
		this.id = season.getId();
		this.name = season.getSeasonName();
	}

	@Override
	public String toString() {
		return "SeasonResDto{"
			+ "id=" + id
			+ ", name='" + name + '\''
			+ '}';
	}
}
