package com.gg.server.domain.noti.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiRepository extends JpaRepository<Noti, Long>, NotiRepositoryCustom {
}
