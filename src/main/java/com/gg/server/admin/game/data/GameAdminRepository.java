package com.gg.server.admin.game.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.season.data.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAdminRepository extends JpaRepository<Game, Long> {

    Page<Game> findBySeason(Pageable pageable, Season season);
}
