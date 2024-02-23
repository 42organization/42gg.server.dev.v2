package gg.pingpong.admin.repo.manage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.pingpong.data.manage.Feedback;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long> {
	@Query(value = "select f from Feedback f join fetch f.user "
		+ "where f.user.intraId = :intraId order by f.user.intraId asc, f.createdAt asc")
	List<Feedback> findFeedbacksByUserIntraId(@Param("intraId") String intraId);
}
