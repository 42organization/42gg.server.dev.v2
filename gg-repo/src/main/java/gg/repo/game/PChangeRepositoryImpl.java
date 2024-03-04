package gg.repo.game;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import gg.data.pingpong.game.PChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PChangeRepositoryImpl implements PChangeRepositoryCustom {
	private final EntityManager em;

	@Override
	public List<PChange> findPChangesHistory(String intraId, Long seasonId) {
		String sql = "select p from PChange p join p.game g join p.user u join g.season s where u.intraId = "
			+ ":intra_id and s.id = :season_id and g.mode = 'RANK' order by p.createdAt desc";
		return em.createQuery(sql, PChange.class)
			.setParameter("intra_id", intraId)
			.setParameter("season_id", seasonId)
			.setFirstResult(0)
			.setMaxResults(10)
			.getResultList();
	}

	@Override
	public List<PChange> findExpHistory(Long userId, Long gameId) {
		String sql = "select p from PChange p join p.game g where p.user.id = "
			+ ":userId and p.id <= (select p2.id from PChange p2 where p2.game.id = :gameId and p2.user.id = :userId) "
			+ "order by p.createdAt desc";
		return em.createQuery(sql, PChange.class)
			.setParameter("userId", userId)
			.setParameter("gameId", gameId)
			.setFirstResult(0)
			.setMaxResults(2)
			.getResultList();
	}

	@Override
	public List<PChange> findPPPHistory(Long userId, Long gameId, Long seasonId) {
		String sql = "select p from PChange p join p.game g join g.season s where p.user.id = "
			+ ":userId and p.id <= (select p2.id from PChange p2 where p2.game.id = :gameId and p2.user.id =:userId) "
			+ "and p.game.mode = 'RANK' and s.id = :season_id "
			+ "order by p.createdAt desc";
		return em.createQuery(sql, PChange.class)
			.setParameter("userId", userId)
			.setParameter("gameId", gameId)
			.setParameter("season_id", seasonId)
			.setFirstResult(0)
			.setMaxResults(2)
			.getResultList();
	}
}
