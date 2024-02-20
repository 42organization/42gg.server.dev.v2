package gg.pingpong.repo.game;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.TeamUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom {
	private final EntityManager em;

	@Override
	public Optional<Game> getLatestGameByUser(Long userId) {
		String sql = "select tu from TeamUser tu join fetch tu.user join fetch tu.team "
			+ "join tu.team.game g where tu.user.id =: userId order by g.startTime desc";

		try {
			TeamUser teamUser = em.createQuery(sql, TeamUser.class).setParameter("userId", userId)
				.setFirstResult(0)
				.setMaxResults(1)
				.getSingleResult();
			return Optional.of(teamUser.getTeam().getGame());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
