package com.gg.server.domain.tournament.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentUserRepository extends JpaRepository<TournamentUser, Long> {

    int countByTournamentAndIsJoined(Tournament tournament, boolean isJoined);

    List<TournamentUser> findAllByTournament(Tournament tournament);

    List<TournamentUser> findAllByTournamentAndIsJoined(Tournament tournament, boolean isJoined);

    List<TournamentUser> findAllByTournamentId(Long tournamentId);
}
