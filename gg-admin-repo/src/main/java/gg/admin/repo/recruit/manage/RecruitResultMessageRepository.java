package gg.admin.repo.recruit.manage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;

public interface RecruitResultMessageRepository extends JpaRepository<ResultMessage, Long> {
	@Transactional
	@Modifying
	@Query("UPDATE ResultMessage rm SET rm.isUse = false WHERE rm.messageType = :messageType")
	void disablePreviousResultMessages(@Param("messageType") MessageType messageType);

	@Query("SELECT r FROM ResultMessage r ORDER BY r.id DESC")
	List<ResultMessage> findAllOrderByIdDesc();

	@Query("SELECT r FROM ResultMessage r "
		+ "WHERE r.messageType = :messageType and r.isUse = true ")
	Optional<ResultMessage> findActiveResultMessageByMessageType(@Param("messageType") MessageType messageType);
}
