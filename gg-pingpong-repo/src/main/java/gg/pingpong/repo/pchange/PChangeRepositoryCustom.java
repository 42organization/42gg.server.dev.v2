package gg.pingpong.repo.pchange;

import java.util.List;

import gg.pingpong.data.game.PChange;

public interface PChangeRepositoryCustom {
	List<PChange> findPChangesHistory(String intraId, Long seasonId);

	List<PChange> findExpHistory(Long userId, Long gameId);

	List<PChange> findPPPHistory(Long userId, Long gameId, Long seasonId);
}
