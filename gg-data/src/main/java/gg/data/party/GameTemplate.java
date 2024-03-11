package gg.data.party;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class GameTemplate extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(length = 20)
	private String gameName;

	@Column
	private Integer maxGamePeople;

	@Column
	private Integer minGamePeople;

	@Column
	private Integer maxGameTime;

	@Column
	private Integer minGameTime;

	@Column(length = 10)
	private String genre;

	@Column(length = 10)
	private String difficulty;

	@Column(length = 100)
	private String summary;

	@Builder
	public GameTemplate(Category category, String gameName, Integer maxGamePeople, Integer minGamePeople,
		Integer maxGameTime, Integer minGameTime, String genre, String difficulty, String summary) {
		this.category = category;
		this.gameName = gameName;
		this.maxGamePeople = maxGamePeople;
		this.minGamePeople = minGamePeople;
		this.maxGameTime = maxGameTime;
		this.minGameTime = minGameTime;
		this.genre = genre;
		this.difficulty = difficulty;
		this.summary = summary;
	}

	public void modifyTemplateDetails(String gameName, Integer maxGamePeople, Integer minGamePeople,
		Integer maxGameTime, Integer minGameTime, String genre,
		String difficulty, String summary) {
		this.gameName = gameName;
		this.maxGamePeople = maxGamePeople;
		this.minGamePeople = minGamePeople;
		this.maxGameTime = maxGameTime;
		this.minGameTime = minGameTime;
		this.genre = genre;
		this.difficulty = difficulty;
		this.summary = summary;
	}

	public void modifyCategory(Category category) {
		this.category = category;
	}

}
