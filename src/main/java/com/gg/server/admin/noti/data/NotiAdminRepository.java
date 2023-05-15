package com.gg.server.admin.noti.data;

import com.gg.server.domain.noti.data.Noti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiAdminRepository extends JpaRepository<Noti, Long> {

}
