package gg.party.api.admin.templates.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateAdminUpdateReqDto {
	private Long categoryId;
	private String gameName;
	private Integer maxGamePeople;
	private Integer minGamePeople;
	private Integer maxGameTime;
	private Integer minGameTime;
	private String genre;
	private String difficulty;
	private String summary;
}
