package gg.data.agenda;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
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
	private byte[] key;

	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Column(name = "content", nullable = false, length = 500)
	private String content;

	@Column(name = "leader_intra_id", nullable = false, length = 30)
	private String leaderIntraId;

	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "location", nullable = false, length = 10)
	private String location;

	@Column(name = "mate_count", nullable = false)
	private int mateCount;

	@Column(name = "award", nullable = false, length = 30)
	private String award;

	@Column(name = "award_priority", nullable = false)
	private int awardPriority;

	@Column(name = "is_private", nullable = false)
	private boolean isPrivate;
}
