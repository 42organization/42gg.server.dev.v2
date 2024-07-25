package gg.data.agenda;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.utils.exception.ErrorCode.*;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.InvalidParameterException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AgendaTeam extends BaseTimeEntity {
	public static final String DEFAULT_AWARD = "participant";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "agenda_id", nullable = false)
	private Agenda agenda;

	@Column(name = "`team_key`", nullable = false, unique = true, columnDefinition = "BINARY(16)")
	private UUID teamKey;

	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Column(name = "content", nullable = false, length = 500)
	private String content;

	@Column(name = "leader_intra_id", nullable = false, length = 30)
	private String leaderIntraId;

	@Column(name = "status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private AgendaTeamStatus status;

	@Column(name = "location", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Location location;

	@Column(name = "mate_count", nullable = false)
	private int mateCount;

	@Column(name = "award", nullable = false, length = 30)
	private String award;

	@Column(name = "award_priority", nullable = false)
	private int awardPriority;

	@Column(name = "is_private", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isPrivate;

	@Builder
	public AgendaTeam(Agenda agenda, UUID teamKey, String name, String content, String leaderIntraId,
		AgendaTeamStatus status, Location location, int mateCount, int awardPriority, Boolean isPrivate) {
		this.agenda = agenda;
		this.teamKey = teamKey;
		this.name = name;
		this.content = content;
		this.leaderIntraId = leaderIntraId;
		this.status = status;
		this.location = location;
		this.mateCount = mateCount;
		this.award = DEFAULT_AWARD;
		this.awardPriority = awardPriority;
		this.isPrivate = isPrivate;
	}

	public void acceptAward(String award, int awardPriority) {
		this.award = award;
		this.awardPriority = awardPriority;
	}

	public void confirm() {
		if (this.status == CANCEL) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CANCEL);
		}
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		this.status = CONFIRM;
	}

	public void leaveTeamLeader() {
		if (this.status == CANCEL) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CANCEL);
		}
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		this.status = CANCEL;
		this.mateCount = 0;
	}

	public void leaveTeamMate() {
		if (this.status == CANCEL) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CANCEL);
		}
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		this.mateCount--;
	}

	public void attendTeam(Agenda agenda) {
		if (this.status == CANCEL) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CANCEL);
		}
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		if (this.mateCount >= agenda.getMaxPeople()) {
			throw new BusinessException(AGENDA_TEAM_FULL);
		}
		this.mateCount++;
	}

	public void updateTeam(String name, String content, Boolean isPrivate, Location location,
		List<AgendaTeamProfile> profiles) {
		if (this.status == CANCEL) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CANCEL);
		}
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		this.name = name;
		this.content = content;
		this.isPrivate = isPrivate;
		updateLocation(location, profiles);
	}

	public void updateLocation(Location location, List<AgendaTeamProfile> profiles) {
		if (Objects.isNull(location)) {
			return;
		}
		boolean conflictAgendaLocation = profiles.stream()
			.map(AgendaTeamProfile::getProfile)
			.anyMatch(profile -> !Location.isUnderLocation(location, profile.getLocation()));
		if (conflictAgendaLocation) {
			throw new InvalidParameterException(UPDATE_LOCATION_NOT_VALID);
		}
		this.location = location;
	}

	public void cancelTeam() {
		if (this.status == CONFIRM) {
			throw new BusinessException(AGENDA_TEAM_ALREADY_CONFIRM);
		}
		this.status = CANCEL;
	}
}
