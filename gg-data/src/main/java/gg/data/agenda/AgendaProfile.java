package gg.data.agenda;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import gg.data.BaseTimeEntity;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "agenda_profile")
public class AgendaProfile extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", length = 1000, nullable = false)
	private String content;

	@Column(name = "github_url", length = 255, nullable = true)
	private String githubUrl;

	@Column(name = "coalition", length = 30, nullable = false)
	@Enumerated(EnumType.STRING)
	private Coalition coalition;

	@Column(name = "location", length = 30, nullable = false)
	@Enumerated(EnumType.STRING)
	private Location location;

	@Column(name = "intra_id", length = 30, nullable = false)
	private String intraId;

	@Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
	private Long userId;

	@Builder
	public AgendaProfile(String content, String githubUrl, Coalition coalition, Location location, String intraId,
		Long userId) {
		this.content = content;
		this.githubUrl = githubUrl;
		this.coalition = coalition;
		this.location = location;
		this.intraId = intraId;
		this.userId = userId;
	}

	public void updateProfile(String content, String githubUrl) {
		this.content = content;
		this.githubUrl = githubUrl;
	}

	public void updateProfileAdmin(String content, String githubUrl, Location location) {
		this.content = content;
		this.githubUrl = githubUrl;
		this.location = location;
	}
}
