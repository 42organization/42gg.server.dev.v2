package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findAllByStatus(TournamentStatus status);

    List<Tournament> findAllByStatusIsNot(TournamentStatus status);

    Page<Tournament> findAllByTypeAndStatus(@NotNull TournamentType type, @NotNull TournamentStatus status, Pageable pageable);

    Page<Tournament> findAllByStatus(@NotNull TournamentStatus status, Pageable pageable);

    Page<Tournament> findAllByType(@NotNull TournamentType type, Pageable pageable);

    boolean existsByTitle(String title);
}
