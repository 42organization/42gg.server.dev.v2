package com.gg.server.domain.tournament.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentUserRepository extends JpaRepository<TournamentUser, Long> {

    int countByTournamentAndIsJoined(Tournament tournament, boolean isJoined);

    int countByTournament(Tournament tournament);
    List<TournamentUser> findAllByTournamentId(Long tournamentId);
}
