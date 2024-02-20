package gg.pingpong.repo.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	Feedback findFirstByOrderByIdDesc();
}
