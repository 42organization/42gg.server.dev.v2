package com.gg.server.admin.feedback.data;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gg.server.data.manage.Feedback;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedbackAdminRepositorySearchImpl implements FeedbackAdminRepositorySearch {

	private final EntityManager em;

	public Page<Feedback> findFeedbacksByUserIntraId(String intraId, Pageable pageable) {
		long feedbackNum = countTotalFeedbacks(intraId, pageable);
		String sql = "select f from Feedback f join fetch f.user where "
			+ "f.user.intraId like \'%" + intraId + "%\' order by f.user.intraId asc, f.createdAt asc";
		List<Feedback> feedbackList = em.createQuery(sql, Feedback.class)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();
		Page<Feedback> feedbackPage = new PageImpl<>(feedbackList, pageable, feedbackNum);
		return feedbackPage;
	}

	private long countTotalFeedbacks(String intraId, Pageable pageable) {
		String sql = "select f from Feedback f join fetch f.user where "
			+ "f.user.intraId like \'%" + intraId + "%\'";
		List<Feedback> feedbackList = em.createQuery(sql, Feedback.class)
			.getResultList();
		return feedbackList.size();
	}
}
