package gg.data.agenda;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AgendaTeam extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "agenda_id", nullable = false)
	private Agenda agenda;

	@Column(name = "`key`", nullable = false, unique = true, columnDefinition = "BINARY(16)")
	private UUID key;

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
	private boolean isPrivate;

	@Builder
	public AgendaTeam(Long id, Agenda agenda, UUID key, String name, String content, String leaderIntraId,
		String status, String location, int mateCount, String award, int awardPriority, boolean isPrivate) {
		this.id = id;
		this.agenda = agenda;
		this.key = key;
		this.name = name;
		this.content = content;
		this.leaderIntraId = leaderIntraId;
		this.status = AgendaTeamStatus.valueOf(status);
		this.location = Location.valueOf(location);
		this.mateCount = mateCount;
		this.award = award;
		this.awardPriority = awardPriority;
		this.isPrivate = isPrivate;
	}
}
