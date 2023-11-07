package com.gg.server.domain.touranment.data;

import com.gg.server.domain.game.data.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
