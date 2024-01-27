package com.gg.server.admin.pchange.data;

import java.util.List;

import com.gg.server.domain.pchange.data.PChange;

public interface PChangeAdminRepositoryCustom {
	List<PChange> findByTeamUser(Long userId);
}
