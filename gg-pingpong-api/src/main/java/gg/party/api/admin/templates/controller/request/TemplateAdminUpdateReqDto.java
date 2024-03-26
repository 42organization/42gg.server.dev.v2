package gg.party.api.admin.templates.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateAdminUpdateReqDto {
	@NotNull(message = "카테고리 ID는 필수입니다.")
	private Long categoryId;

	@NotBlank(message = "게임 이름은 필수이며, 비어 있을 수 없습니다.")
	@Size(max = 15, message = "게임 이름은 15자를 초과할 수 없습니다.")
	private String gameName;

	@NotNull(message = "최대 게임 인원은 필수입니다.")
	@Min(value = 2, message = "최대 게임 인원은 최소 2명 이상이어야 합니다.")
	private Integer maxGamePeople;

	@NotNull(message = "최소 게임 인원은 필수입니다.")
	@Min(value = 2, message = "최소 게임 인원은 최소 2명 이상이어야 합니다.")
	private Integer minGamePeople;

	@NotNull(message = "최대 게임 시간은 필수입니다.")
	@Min(value = 1, message = "최대 게임 시간은 최소 1분 이상이어야 합니다.")
	private Integer maxGameTime;

	@NotNull(message = "최소 게임 시간은 필수입니다.")
	@Min(value = 1, message = "최소 게임 시간은 최소 1분 이상이어야 합니다.")
	private Integer minGameTime;

	@NotBlank(message = "장르는 필수이며, 비어 있을 수 없습니다.")
	@Size(max = 10, message = "장르는 10자를 초과할 수 없습니다.")
	private String genre;

	@Size(max = 10, message = "난이도는 10자를 초과할 수 없습니다.")
	private String difficulty;

	@NotBlank(message = "내용은 필수이며, 비어 있을 수 없습니다.")
	@Size(max = 100, message = "내용은 100자를 초과할 수 없습니다.")
	private String summary;

	public TemplateAdminUpdateReqDto(long categoryId, String gameName, int maxGamePeople, int minGamePeople,
		int maxGameTime, int minGameTime, String genre, String difficulty, String summary) {
		this.categoryId = categoryId;
		this.gameName = gameName;
		this.maxGamePeople = maxGamePeople;
		this.minGamePeople = minGamePeople;
		this.maxGameTime = maxGameTime;
		this.minGameTime = minGameTime;
		this.genre = genre;
		this.difficulty = difficulty;
		this.summary = summary;
	}
}
