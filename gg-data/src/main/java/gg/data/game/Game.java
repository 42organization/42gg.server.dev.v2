package gg.data.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.season.Season;
import gg.utils.exception.BusinessChecker;
import gg.utils.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@DynamicUpdate
public class Game {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "season_id")
	private Season season;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10)
	private StatusType status;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "mode", length = 10)
	private Mode mode;

	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private List<Team> teams = new ArrayList<>();

	public Game(Season season, StatusType status, Mode mode, LocalDateTime startTime, LocalDateTime endTime) {
		this.season = season;
		this.status = status;
		this.mode = mode;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Game(Season season, Mode mode, LocalDateTime startTime, Integer interval) {
		this.season = season;
		this.status = StatusType.BEFORE;
		this.mode = mode;
		this.startTime = startTime;
		this.endTime = startTime.plusMinutes(interval);
	}

	@Override
	public String toString() {
		return "Game{"
			+ "id=" + id
			+ ", season=" + season
			+ ", status=" + status
			+ ", mode=" + mode
			+ ", startTime=" + startTime
			+ ", endTime=" + endTime
			+ '}';
	}

	public void updateStatus() {
		if (status == StatusType.BEFORE) {
			this.status = StatusType.LIVE;
		} else if (status == StatusType.LIVE) {
			this.status = StatusType.WAIT;
		} else {
			this.status = StatusType.END;
		}
	}

	public void addTeam(Team team) {
		BusinessChecker.mustNotNull(team, ErrorCode.NULL_POINT);
		BusinessChecker.mustNotExceed(1, teams, ErrorCode.TEAM_SIZE_EXCEED);
		BusinessChecker.mustNotContains(team, teams, ErrorCode.TEAM_DUPLICATION);
		this.teams.add(team);
	}

}
