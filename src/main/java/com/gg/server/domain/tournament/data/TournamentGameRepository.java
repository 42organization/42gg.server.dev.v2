package com.gg.server.domain.tournament.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
}
