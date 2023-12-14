package com.gg.server.domain.tournament.data;

import com.gg.server.domain.user.data.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentUserRepository extends JpaRepository<TournamentUser, Long> {

    int countByTournamentAndIsJoined(Tournament tournament, boolean isJoined);

    List<TournamentUser> findAllByTournament(Tournament tournament);

    List<TournamentUser> findAllByTournamentAndIsJoined(Tournament tournament, boolean isJoined);

    List<TournamentUser> findAllByTournamentId(Long tournamentId);

    Optional<TournamentUser> findByTournamentIdAndUserId(Long tournamentId, Long userId);

    List<TournamentUser> findAllByUser(User user);
}
