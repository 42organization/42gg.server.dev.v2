package gg.pingpong.api.global.scheduler;

import org.springframework.stereotype.Component;

import gg.pingpong.api.global.config.ConstantConfig;
import gg.pingpong.api.user.tournament.service.TournamentService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TournamentScheduler extends AbstractScheduler {
	private final TournamentService tournamentService;

	private final ConstantConfig constantConfig;

	public TournamentScheduler(TournamentService tournamentService, ConstantConfig constantConfig) {
		this.tournamentService = tournamentService;
		this.constantConfig = constantConfig;
		this.cron = constantConfig.getTournamentSchedule(); // TODO QA 이후 0 0 0 * * * 로 변경
	}

	@Override
	public Runnable runnable() {
		return () -> {
			log.info("Tournament Scheduler Started");
			tournamentService.startTournament();
		};
	}
}
