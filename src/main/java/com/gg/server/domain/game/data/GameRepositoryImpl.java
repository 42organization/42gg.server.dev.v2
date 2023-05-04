package com.gg.server.domain.game.data;

import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.TeamUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom{
    private final EntityManager em;
    @Override
    public Optional<Game> getLatestGameByUser(Long userId) {
        String sql = "select tu from TeamUser tu join fetch tu.user join fetch tu.team " +
                "join tu.team.game g where tu.user.id =: userId order by g.startTime desc";

        try{
            TeamUser teamUser = em.createQuery(sql, TeamUser.class).setParameter("userId", userId)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();
            return Optional.of(teamUser.getTeam().getGame());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }
}
