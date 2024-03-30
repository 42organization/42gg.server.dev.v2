package gg.party.api.user.template.controller.response;

import gg.data.party.GameTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TemplateResDto {
	private Long gameTemplateId;
	private String categoryName;
	private String gameName;
	private Integer maxGamePeople;
	private Integer minGamePeople;
	private Integer maxGameTime;
	private Integer minGameTime;
	private String genre;
	private String difficulty;
	private String summary;

	public TemplateResDto(GameTemplate template) {
		this.gameTemplateId = template.getId();
		this.categoryName = template.getCategory().getName();
		this.gameName = template.getGameName();
		this.maxGamePeople = template.getMaxGamePeople();
		this.minGamePeople = template.getMinGamePeople();
		this.maxGameTime = template.getMaxGameTime();
		this.minGameTime = template.getMinGameTime();
		this.genre = template.getGenre();
		this.difficulty = template.getDifficulty();
		this.summary = template.getSummary();
	}
}
