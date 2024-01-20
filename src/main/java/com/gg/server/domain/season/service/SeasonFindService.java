package com.gg.server.domain.season.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.exception.SeasonNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SeasonFindService {
	private final SeasonRepository seasonRepository;
	private final GameRepository gameRepository;

	@Transactional(readOnly = true)
	public Season findCurrentSeason(LocalDateTime now) {
		return seasonRepository.findCurrentSeason(now).orElseThrow(() -> new SeasonNotFoundException());
	}

	@Transactional(readOnly = true)
	public Season findSeasonById(Long seasonId) {
		return seasonRepository.findById(seasonId).orElseThrow(() -> new SeasonNotFoundException());
	}

	@Transactional(readOnly = true)
	public Season findSeasonByGameId(Long gameId) {
		Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotExistException());
		return game.getSeason();
	}
}
