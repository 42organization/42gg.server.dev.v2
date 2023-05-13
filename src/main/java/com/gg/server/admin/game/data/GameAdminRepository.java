package com.gg.server.admin.game.data;

import com.gg.server.domain.game.data.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAdminRepository extends JpaRepository<Game, Long> {

    Page<Game> findAllByOrderByStartTimeDesc(Pageable pageable);
//    @Override
//    @Query("select g from Game g order by g.startTime desc")
//    List<Game> findAll();

//    @Query("select g from Game g join fetch g.slot where g.season = :seasonId order by g.id desc")
//    List<Game> findBySeason(@Param("seasonId") int seasonId);
}
