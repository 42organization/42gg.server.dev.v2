package gg.pingpong.admin.repo.noti;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.noti.Noti;

public interface NotiAdminRepository
	extends JpaRepository<Noti, Long>, NotiAdminRepositoryCustom {

	@Override
	@EntityGraph(attributePaths = {"user"})
	Page<Noti> findAll(Pageable pageable);

}
