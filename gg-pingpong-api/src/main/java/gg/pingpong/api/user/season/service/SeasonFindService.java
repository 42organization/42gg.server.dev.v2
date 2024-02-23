package gg.pingpong.api.user.season.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.data.game.Game;
import gg.pingpong.data.season.Season;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.season.SeasonRepository;
import gg.pingpong.utils.exception.game.GameNotExistException;
import gg.pingpong.utils.exception.season.SeasonNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SeasonFindService {
	private final SeasonRepository seasonRepository;
	private final GameRepository gameRepository;

	@Transactional(readOnly = true)
	public Season findCurrentSeason(LocalDateTime now) {
		return seasonRepository.findCurrentSeason(now).orElseThrow(SeasonNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Season findSeasonById(Long seasonId) {
		return seasonRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Season findSeasonByGameId(Long gameId) {
		Game game = gameRepository.findById(gameId).orElseThrow(GameNotExistException::new);
		return game.getSeason();
	}
}
