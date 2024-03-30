package gg.recruit.api.admin.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatedRecruitmentResponse {
	private Long id;

	public CreatedRecruitmentResponse(Long id) {
		this.id = id;
	}
}
