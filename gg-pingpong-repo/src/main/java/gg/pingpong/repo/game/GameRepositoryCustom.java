package gg.pingpong.repo.game;

import java.util.Optional;

import gg.pingpong.data.game.Game;

public interface GameRepositoryCustom {
	Optional<Game> getLatestGameByUser(Long userId);
}
