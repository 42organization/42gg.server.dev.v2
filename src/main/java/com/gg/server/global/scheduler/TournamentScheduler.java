package com.gg.server.global.scheduler;

import com.gg.server.domain.tournament.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TournamentScheduler extends AbstractScheduler {
    private final TournamentService tournamentService;

    public TournamentScheduler(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
        this.cron = "0 */10 0 * * *";       // TODO QA 이후 0 0 0 * * * 로 변경
    }

    @Override
    public Runnable runnable() {
        return () -> {
            log.info("Tournament Scheduler Started");
            tournamentService.startTournament();
        };
    }
}
