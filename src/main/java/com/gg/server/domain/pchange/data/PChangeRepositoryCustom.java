package com.gg.server.domain.pchange.data;

import java.util.List;

public interface PChangeRepositoryCustom {

    List<PChange> findPChangesHistory(String intraId, Long seasonId);
    List<PChange> findExpHistory(String intraId, Long gameId);
    List<PChange> findPPPHistory(String intraId, Long gameId);
}
