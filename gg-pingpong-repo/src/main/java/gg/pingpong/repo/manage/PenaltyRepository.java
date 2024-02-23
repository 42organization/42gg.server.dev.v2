package gg.pingpong.repo.manage;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.manage.Penalty;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

}
