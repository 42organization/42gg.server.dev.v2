package gg.repo.recruit.recruitment;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecruitmentRepositoryCustomImpl implements RecruitmentRepositoryCustom {

	private final EntityManager em;

}
