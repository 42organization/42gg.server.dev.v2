package com.gg.server.domain.penalty.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.Penalty;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

}
