package com.gg.server.domain.pchange.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PChangeRepositoryImpl implements PChangeRepositoryCustom{
    private final EntityManager em;
    @Override
    public List<PChange> findPChangesHistory(String intraId, Long seasonId) {
        String sql = "select p from PChange p join fetch p.game g join g.season s where p.user.intraId = " +
                ":intra_id and s.id = :season_id order by p.createdAt desc";
        return em.createQuery(sql, PChange.class)
                .setParameter("intra_id", intraId)
                .setParameter("season_id", seasonId)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();
    }
}
