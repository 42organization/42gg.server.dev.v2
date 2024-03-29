package gg.admin.repo.manage;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gg.data.manage.Penalty;

public interface PenaltyAdminRepositoryCustom {
	Page<Penalty> findAllCurrent(Pageable pageable, LocalDateTime targetTime);

	Page<Penalty> findAllByIntraId(Pageable pageable, String intraId);

	Page<Penalty> findAllCurrentByIntraId(Pageable pageable, LocalDateTime targetTime,
		String intraId);
}
