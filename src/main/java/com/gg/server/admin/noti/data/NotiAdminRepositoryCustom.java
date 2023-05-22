package com.gg.server.admin.noti.data;

import com.gg.server.domain.noti.data.Noti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface NotiAdminRepositoryCustom {

    Page<Noti> findNotisByUserIntraId(Pageable pageable, @Param("intraId") String intraId);
}
