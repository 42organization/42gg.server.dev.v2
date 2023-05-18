package com.gg.server.domain.pchange.data;

import java.util.List;

public interface PChangeRepositoryCustom {

    List<PChange> findPChangesHistory(String intraId, Long seasonId);
}
