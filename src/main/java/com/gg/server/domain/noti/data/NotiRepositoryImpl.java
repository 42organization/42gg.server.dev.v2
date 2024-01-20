package com.gg.server.domain.noti.data;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotiRepositoryImpl implements NotiRepositoryCustom {
	private final EntityManager em;

	@Override
	public int countNotCheckedNotiByUser(Long userId) {
		String sql = "select count(n) from Noti n where n.isChecked=false and user.id =: userId";
		Long cntNoti = em.createQuery(sql, Long.class)
			.setParameter("userId", userId)
			.getSingleResult();
		return cntNoti.intValue();
	}
}
