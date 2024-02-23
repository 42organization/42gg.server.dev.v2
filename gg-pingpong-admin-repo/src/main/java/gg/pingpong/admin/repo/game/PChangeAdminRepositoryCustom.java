package gg.pingpong.admin.repo.game;

import java.util.List;

import gg.pingpong.data.game.PChange;

public interface PChangeAdminRepositoryCustom {
	List<PChange> findByTeamUser(Long userId);
}
