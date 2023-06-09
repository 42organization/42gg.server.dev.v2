package com.gg.server.admin.pchange.data;

import com.gg.server.domain.pchange.data.PChange;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@RequiredArgsConstructor
public class PChangeAdminRepositoryCustomImpl implements PChangeAdminRepositoryCustom {
    private final EntityManager em;
    @Override
    public List<PChange> findByTeamUser(Long userId) {
        String query = "SELECT p FROM PChange p WHERE p.user.id = :userId ORDER BY p.createdAt desc";
        return em.createQuery(query, PChange.class)
                .setParameter("userId", userId)
                .setMaxResults(2)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }
}
