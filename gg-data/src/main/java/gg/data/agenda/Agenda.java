package gg.data.agenda;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.core.io.support.ResourcePatternUtils;

import gg.data.BaseTimeEntity;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.InvalidParameterException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "agenda", uniqueConstraints = {
	@UniqueConstraint(name = "uk_agenda_agenda_key", columnNames = "agenda_key")
})
public class Agenda extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agenda_key", nullable = false, unique = true, columnDefinition = "BINARY(16)")
	private UUID agendaKey;

	@Column(name = "title", nullable = false, columnDefinition = "VARCHAR(50)")
	private String title;

	@Column(name = "content", nullable = false, columnDefinition = "VARCHAR(500)")
	private String content;

	@Column(name = "deadline", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime deadline;

	@Column(name = "start_time", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime endTime;

	@Column(name = "min_team", nullable = false, columnDefinition = "INT")
	private int minTeam;

	@Column(name = "max_team", nullable = false, columnDefinition = "INT")
	private int maxTeam;

	@Column(name = "current_team", nullable = false, columnDefinition = "INT")
	private int currentTeam;

	@Column(name = "min_people", nullable = false, columnDefinition = "INT")
	private int minPeople;

	@Column(name = "max_people", nullable = false, columnDefinition = "INT")
	private int maxPeople;

	@Column(name = "poster_uri", columnDefinition = "VARCHAR(255)")
	private String posterUri;

	@Column(name = "host_intra_id", nullable = false, columnDefinition = "VARCHAR(30)")
	private String hostIntraId;

	@Column(name = "location", nullable = false, columnDefinition = "VARCHAR(30)")
	@Enumerated(EnumType.STRING)
	private Location location;

	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10)")
	@Enumerated(EnumType.STRING)
	private AgendaStatus status;

	@Column(name = "is_official", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isOfficial;

	@Column(name = "is_ranking", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isRanking;

	@Builder
	public Agenda(Long id, String title, String content, LocalDateTime deadline,
		LocalDateTime startTime, LocalDateTime endTime, int minTeam, int maxTeam, int currentTeam,
		int minPeople, int maxPeople, String posterUri, String hostIntraId, Location location,
		AgendaStatus status, Boolean isOfficial, Boolean isRanking) {
		this.id = id;
		this.agendaKey = UUID.randomUUID();
		this.title = title;
		this.content = content;
		this.deadline = deadline;
		this.startTime = startTime;
		this.endTime = endTime;
		this.minTeam = minTeam;
		this.maxTeam = maxTeam;
		this.currentTeam = currentTeam;
		this.minPeople = minPeople;
		this.maxPeople = maxPeople;
		this.posterUri = posterUri;
		this.hostIntraId = hostIntraId;
		this.location = location;
		this.status = status;
		this.isOfficial = isOfficial;
		this.isRanking = isRanking;
	}

	public void confirmAgenda() {
		if (this.status == AgendaStatus.FINISH) {
			throw new InvalidParameterException(AGENDA_ALREADY_FINISHED);
		}
		if (this.status == AgendaStatus.CANCEL) {
			throw new InvalidParameterException(AGENDA_ALREADY_CANCELED);
		}
		if (this.status == AgendaStatus.CONFIRM) {
			throw new InvalidParameterException(AGENDA_ALREADY_CONFIRMED);
		}
		this.status = AgendaStatus.CONFIRM;
	}

	public void finishAgenda() {
		if (this.status == AgendaStatus.OPEN) {
			throw new InvalidParameterException(AGENDA_DOES_NOT_CONFIRM);
		}
		if (this.status == AgendaStatus.CANCEL) {
			throw new InvalidParameterException(AGENDA_ALREADY_CANCELED);
		}
		if (this.status == AgendaStatus.FINISH) {
			throw new InvalidParameterException(AGENDA_ALREADY_FINISHED);
		}
		this.status = AgendaStatus.FINISH;
	}

	public void updateInformation(String title, String content) {
		if (Objects.nonNull(title) && !title.isBlank()) {
			this.title = title;
		}
		if (Objects.nonNull(content) && !content.isBlank()) {
			this.content = content;
		}
	}

	public void updatePosterUri(String posterUri) {
		if (Objects.nonNull(posterUri) && ResourcePatternUtils.isUrl(posterUri)) {
			this.posterUri = posterUri;
		}
	}

	public void updateIsOfficial(Boolean isOfficial) {
		if (Objects.nonNull(isOfficial)) {
			this.isOfficial = isOfficial;
		}
	}

	public void updateIsRanking(Boolean isRanking) {
		if (Objects.nonNull(isRanking)) {
			this.isRanking = isRanking;
		}
	}

	public void updateAgendaStatus(AgendaStatus agendaStatus) {
		if (Objects.nonNull(agendaStatus)) {
			this.status = agendaStatus;
		}
	}

	public void updateSchedule(LocalDateTime deadline, LocalDateTime startTime, LocalDateTime endTime) {
		if (Objects.isNull(deadline) || Objects.isNull(startTime) || Objects.isNull(endTime)) {
			return;
		}
		mustHaveValidSchedule();
		this.deadline = deadline;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void updateLocation(Location location, List<AgendaTeam> teams) {
		if (Objects.isNull(location)) {
			return;
		}
		boolean conflictAgendaLocation = teams.stream()
			.map(AgendaTeam::getLocation)
			.anyMatch(teamLocation -> !Location.isUnderLocation(location, teamLocation));
		if (conflictAgendaLocation) {
			throw new InvalidParameterException(UPDATE_LOCATION_NOT_VALID);
		}
		this.location = location;
	}

	public void updateAgendaCapacity(int minTeam, int maxTeam, List<AgendaTeam> teams) {
		if (minTeam < 2 || maxTeam < 2) {
			return;
		}
		if (minTeam > maxTeam || teams.size() > maxTeam) {
			throw new InvalidParameterException(AGENDA_CAPACITY_CONFLICT);
		}
		if (this.status == AgendaStatus.FINISH && teams.size() < minTeam) {
			throw new InvalidParameterException(AGENDA_CAPACITY_CONFLICT);
		}
		this.minTeam = minTeam;
		this.maxTeam = maxTeam;
	}

	public void updateAgendaTeamCapacity(int minPeople, int maxPeople, List<AgendaTeam> teams) {
		if (minPeople < 1 || maxPeople < 1) {
			return;
		}
		if (minPeople > maxPeople) {
			throw new InvalidParameterException(AGENDA_INVALID_PARAM);
		}
		boolean conflictAgendaTeamCapacity = teams.stream()
			.anyMatch(team -> team.getMateCount() > maxPeople
				|| (team.getStatus() == AgendaTeamStatus.CONFIRM && team.getMateCount() < minPeople));
		if (conflictAgendaTeamCapacity) {
			throw new InvalidParameterException(AGENDA_TEAM_CAPACITY_CONFLICT);
		}
		this.minPeople = minPeople;
		this.maxPeople = maxPeople;
	}

	public void addTeam(Location location, LocalDateTime now) {
		mustBeWithinLocation(location);
		mustStatusOpen();
		mustBeforeDeadline(now);
		mustHaveCapacity();
	}

	public void confirmTeam(Location location, LocalDateTime now) {
		mustBeWithinLocation(location);
		mustStatusOpen();
		mustBeforeDeadline(now);
		mustHaveCapacity();
		this.currentTeam++;
	}

	public void attendTeam(Location location, LocalDateTime now) {
		mustBeWithinLocation(location);
		mustStatusOpen();
		mustBeforeDeadline(now);
	}

	public void updateTeam(Location location, LocalDateTime now) {
		mustBeWithinLocation(location);
		mustStatusOpen();
		mustBeforeDeadline(now);
	}

	public void leaveTeam(LocalDateTime now) {
		mustStatusOpen();
		mustBeforeDeadline(now);
	}

	private void mustBeWithinLocation(Location location) {
		if (this.location != Location.MIX && this.location != location) {
			throw new InvalidParameterException(LOCATION_NOT_VALID);
		}
	}

	private void mustStatusOpen() {
		if (this.status != AgendaStatus.OPEN) {
			throw new InvalidParameterException(AGENDA_NOT_OPEN);
		}
	}

	private void mustBeforeDeadline(LocalDateTime now) {
		if (this.deadline.isBefore(now)) {
			throw new InvalidParameterException(AGENDA_NOT_OPEN);
		}
	}

	private void mustHaveCapacity() {
		if (this.currentTeam == this.maxTeam) {
			throw new ForbiddenException(AGENDA_NO_CAPACITY);
		}
	}

	private void mustHaveValidSchedule() {
		if (this.deadline.isAfter(this.startTime)) {
			throw new InvalidParameterException(AGENDA_INVALID_PARAM);
		}
		if (this.startTime.isAfter(this.endTime)) {
			throw new InvalidParameterException(AGENDA_INVALID_PARAM);
		}
	}

	public void mustModifiedByHost(String userIntraId) {
		if (this.hostIntraId.equals(userIntraId)) {
			return;
		}
		throw new ForbiddenException(AGENDA_MODIFICATION_FORBIDDEN);
	}
}
