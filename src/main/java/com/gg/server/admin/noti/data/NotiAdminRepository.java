package com.gg.server.admin.noti.data;

import com.gg.server.domain.noti.data.Noti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiAdminRepository
        extends JpaRepository<Noti, Long>, NotiAdminRepositoryCustom{

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<Noti> findAll(Pageable pageable);

}
