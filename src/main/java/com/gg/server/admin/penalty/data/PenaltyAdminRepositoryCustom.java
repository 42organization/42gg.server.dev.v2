package com.gg.server.admin.penalty.data;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gg.server.domain.penalty.data.Penalty;

public interface PenaltyAdminRepositoryCustom {
	Page<Penalty> findAllCurrent(Pageable pageable, LocalDateTime targetTime);

	Page<Penalty> findAllByIntraId(Pageable pageable, String intraId);

	Page<Penalty> findAllCurrentByIntraId(Pageable pageable, LocalDateTime targetTime,
		String intraId);
}
