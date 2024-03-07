package gg.party.api.user.templates.controller.response;

import gg.data.party.GameTemplate;
import lombok.Getter;

@Getter
public class TemplatesResDto {
	private Long gameTemplateId;
	private Long categoryId;
	private String gameName;
	private Integer maxGamePeople;
	private Integer minGamePeople;
	private Integer maxGameTime;
	private Integer minGameTime;
	private String genre;
	private String difficulty;
	private String summary;

	public TemplatesResDto(GameTemplate template) {
		this.gameTemplateId = template.getId();
		this.categoryId = template.getCategory().getId();
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
