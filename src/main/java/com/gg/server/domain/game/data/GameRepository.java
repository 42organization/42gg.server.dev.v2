package com.gg.server.domain.game.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom{
}
