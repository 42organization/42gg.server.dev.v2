package com.gg.server.domain.pchange.data;

import java.util.List;

public interface PChangeRepositoryCustom {

    List<PChange> findPChangesHistory(Long userId, Long seasonId);
}
