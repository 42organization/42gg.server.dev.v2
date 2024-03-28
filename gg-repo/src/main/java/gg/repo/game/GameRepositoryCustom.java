package gg.repo.game;

import java.util.Optional;

import gg.data.pingpong.game.Game;

public interface GameRepositoryCustom {
	Optional<Game> getLatestGameByUser(Long userId);
}
