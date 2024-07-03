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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AgendaTeamProfile extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "profile_id", nullable = false)
	private AgendaProfile profile;

	@ManyToOne
	@JoinColumn(name = "agenda_team_id", nullable = false)
	private AgendaTeam agendaTeam;

	@Column(name = "is_exist", nullable = false, columnDefinition = "BIT(1)")
	private boolean isExist;
}
