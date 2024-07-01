package gg.data.agenda;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
@Table(name = "agenda_profile")
public class AgendaProfile extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", length = 1000, nullable = false)
	private String content;

	@Column(name = "github_url", length = 255, nullable = false)
	private String githubUrl;

	@Column(name = "coalition", length = 30, nullable = false)
	private String coalition;

	@Column(name = "location", length = 30, nullable = false)
	private String location;

	public AgendaProfile(String content, String githubUrl, String coalition, String location) {
		this.content = content;
		this.githubUrl = githubUrl;
		this.coalition = coalition;
		this.location = location;
	}
}
