package gg.pingpong.repo.game;

import java.util.Optional;

import com.gg.server.data.game.Game;

public interface GameRepositoryCustom {
	Optional<Game> getLatestGameByUser(Long userId);
}
