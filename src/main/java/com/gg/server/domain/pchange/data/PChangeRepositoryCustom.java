package com.gg.server.domain.pchange.data;

import java.util.List;

public interface PChangeRepositoryCustom {

    List<PChange> findPChangesHistory(String intraId, Long seasonId);
    List<PChange> findExpHistory(Long userId, Long gameId);
    List<PChange> findPPPHistory(Long userId, Long gameId, Long seasonId);
}
