package gg.admin.repo.game;

import java.util.List;

import gg.data.game.PChange;

public interface PChangeAdminRepositoryCustom {
	List<PChange> findByTeamUser(Long userId);
}
