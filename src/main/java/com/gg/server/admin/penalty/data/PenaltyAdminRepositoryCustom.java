package com.gg.server.admin.penalty.data;

import com.gg.server.domain.penalty.data.Penalty;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface PenaltyAdminRepositoryCustom {
    Page<Penalty> findAllCurrent(Pageable pageable, LocalDateTime targetTime);
    Page<Penalty> findAllByIntraId(Pageable pageable, String intraId);
    Page<Penalty> findAllCurrentByIntraId(Pageable pageable, LocalDateTime targetTime,
                                          String intraId);
}
