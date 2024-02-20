package gg.pingpong.admin.repo.penalty;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import gg.pingpong.data.manage.Penalty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PenaltyAdminRepositoryCustomImpl implements PenaltyAdminRepositoryCustom {
	private final EntityManager em;

	@Override
	public Page<Penalty> findAllCurrent(Pageable pageable, LocalDateTime targetTime) {
		String sql =
			"select p from Penalty p where TIME_TO_SEC(TIMEDIFF(:targetTime, p.startTime)) < p.penaltyTime * 60 "
				+ "order by p.startTime desc";
		long count = countCurrent(targetTime);
		List<Penalty> penalties = em.createQuery(sql, Penalty.class)
			.setParameter("targetTime", targetTime)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();
		Page<Penalty> result = new PageImpl<>(penalties, pageable, count);
		return result;
	}

	@Override
	public Page<Penalty> findAllByIntraId(Pageable pageable, String intraId) {
		String sql = "SELECT p FROM Penalty p JOIN FETCH p.user "
			+ "WHERE p.user.intraId LIKE :intraId ORDER BY p.startTime DESC";
		long count = countByIntraId(intraId);
		List<Penalty> penalties = em.createQuery(sql, Penalty.class)
			.setParameter("intraId", "%" + intraId + "%")
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();
		Page<Penalty> result = new PageImpl<>(penalties, pageable, count);
		return result;
	}

	@Override
	public Page<Penalty> findAllCurrentByIntraId(Pageable pageable, LocalDateTime targetTime, String intraId) {
		String sql = "select p from Penalty p join fetch p.user where "
			+ "p.user.intraId like :intraId and TIME_TO_SEC(TIMEDIFF(:targetTime, p.startTime)) < p.penaltyTime * 60 "
			+ "order by p.startTime desc";
		long count = countCurrentByIntraId(intraId, targetTime);
		List<Penalty> penalties = em.createQuery(sql, Penalty.class)
			.setParameter("intraId", "%" + intraId + "%")
			.setParameter("targetTime", targetTime)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();
		Page<Penalty> result = new PageImpl<>(penalties, pageable, count);
		return result;
	}

	private long countCurrent(LocalDateTime targetTime) {
		String sql =
			"select p from Penalty p where TIME_TO_SEC(TIMEDIFF(:targetTime, p.startTime)) < p.penaltyTime * 60 "
				+ "order by p.startTime desc";
		return em.createQuery(sql, Penalty.class)
			.setParameter("targetTime", targetTime)
			.getResultList().size();
	}

	private long countByIntraId(String intraId) {
		String sql = "select p FROM Penalty p join fetch p.user u WHERE u.intraId LIKE :intraId "
			+ "order by p.startTime desc";
		return em.createQuery(sql, Penalty.class)
			.setParameter("intraId", "%" + intraId + "%")
			.getResultList()
			.size();
	}

	private long countCurrentByIntraId(String intraId, LocalDateTime targetTime) {
		String sql = "select p from Penalty p join fetch p.user where "
			+ "p.user.intraId like :intraId and TIME_TO_SEC(TIMEDIFF(:targetTime, p.startTime)) < p.penaltyTime * 60 "
			+ "order by p.startTime desc";
		return em.createQuery(sql, Penalty.class)
			.setParameter("intraId", "%" + intraId + "%")
			.setParameter("targetTime", targetTime)
			.getResultList()
			.size();

	}
}
