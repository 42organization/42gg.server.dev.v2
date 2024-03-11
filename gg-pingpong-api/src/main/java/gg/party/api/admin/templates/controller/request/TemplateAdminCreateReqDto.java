package gg.party.api.admin.templates.controller.request;

import gg.data.party.Category;
import gg.data.party.GameTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateAdminCreateReqDto {
	private Long categoryId;
	private String gameName;
	private Integer maxGamePeople;
	private Integer minGamePeople;
	private Integer maxGameTime;
	private Integer minGameTime;
	private String genre;
	private String difficulty;
	private String summary;

	public static GameTemplate toEntity(TemplateAdminCreateReqDto dto, Category category) {
		return GameTemplate.builder()
			.category(category)
			.gameName(dto.getGameName())
			.maxGamePeople(dto.getMaxGamePeople())
			.minGamePeople(dto.getMinGamePeople())
			.maxGameTime(dto.getMaxGameTime())
			.minGameTime(dto.getMinGameTime())
			.genre(dto.getGenre())
			.difficulty(dto.getDifficulty())
			.summary(dto.getSummary())
			.build();
	}
}
