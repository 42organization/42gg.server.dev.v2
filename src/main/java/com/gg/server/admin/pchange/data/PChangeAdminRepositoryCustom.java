package com.gg.server.admin.pchange.data;

import java.util.List;

import com.gg.server.data.game.PChange;

public interface PChangeAdminRepositoryCustom {
	List<PChange> findByTeamUser(Long userId);
}
