package com.gg.server.admin.noti.data;

import com.gg.server.domain.noti.data.Noti;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class NotiAdminRepositoryCustomImpl implements NotiAdminRepositoryCustom {
    private final EntityManager em;
    @Override
    public Page<Noti> findNotisByUserIntraId(Pageable pageable, String intraId) {
        long totalElem = countTotalElem(intraId);
        String sql = "select n from Noti n join fetch n.user where " +
                "n.user.intraId like \'%" + intraId + "%\' order by n.createdAt desc, n.user.intraId asc";
        List<Noti> notis = em.createQuery(sql, Noti.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Page<Noti> result = new PageImpl<>(notis, pageable, totalElem);
        return result;
    }

    private long countTotalElem(String intraId) {
        String sql = "select n from Noti n join fetch n.user where " +
                "n.user.intraId like \'%" + intraId + "%\'";
        List<Noti> notiList = em.createQuery(sql, Noti.class)
                .getResultList();
        return notiList.size();
    }
}
