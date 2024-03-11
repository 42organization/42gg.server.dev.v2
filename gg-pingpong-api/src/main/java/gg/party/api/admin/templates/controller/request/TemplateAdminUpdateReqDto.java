package gg.party.api.admin.templates.controller.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateAdminUpdateReqDto {
	@NotNull(message = "Category must not be null")
	private Long categoryId;

	@NotNull(message = "Game name must not be null")
	private String gameName;

	@NotNull(message = "Maximum game people must not be null")
	private Integer maxGamePeople;

	@NotNull(message = "Minimum game people must not be null")
	private Integer minGamePeople;

	@NotNull(message = "Maximum game time must not be null")
	private Integer maxGameTime;

	@NotNull(message = "Minimum game time must not be null")
	private Integer minGameTime;

	private String genre;
	private String difficulty;

	@NotNull(message = "Summary must not be null")
	private String summary;
}