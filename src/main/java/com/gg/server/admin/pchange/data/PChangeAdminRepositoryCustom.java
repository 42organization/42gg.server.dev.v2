package com.gg.server.admin.pchange.data;

import com.gg.server.domain.pchange.data.PChange;

import java.util.List;

public interface PChangeAdminRepositoryCustom {
    List<PChange> findByTeamUser(Long userId);
}
