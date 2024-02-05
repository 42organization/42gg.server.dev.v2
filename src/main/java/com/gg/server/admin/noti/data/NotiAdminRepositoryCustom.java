package com.gg.server.admin.noti.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.noti.Noti;

public interface NotiAdminRepositoryCustom {

	Page<Noti> findNotisByUserIntraId(Pageable pageable, @Param("intraId") String intraId);
}
