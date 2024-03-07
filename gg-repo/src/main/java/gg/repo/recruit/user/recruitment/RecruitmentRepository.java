package gg.repo.recruit.user.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.recruitment.Recruitments;

public interface RecruitmentRepository extends JpaRepository<Recruitments, Long> {
}
