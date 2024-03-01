package gg.pingpong.api.user.season.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gg.pingpong.api.user.season.controller.response.SeasonResDto;
import gg.pingpong.api.user.season.dto.CurSeason;
import gg.repo.season.SeasonRepository;
import gg.utils.exception.season.SeasonNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeasonService {
	private final SeasonRepository seasonRepository;

	public List<SeasonResDto> seasonList() {
		return seasonRepository.findActiveSeasonsDesc(LocalDateTime.now())
			.stream().map(SeasonResDto::new).collect(Collectors.toList());
	}

	public CurSeason getCurSeason() {
		return new CurSeason(seasonRepository.findCurrentSeason(LocalDateTime.now())
			.orElseThrow(() -> new SeasonNotFoundException("현재 시즌이 존재하지 않습니다.")));
	}
}
