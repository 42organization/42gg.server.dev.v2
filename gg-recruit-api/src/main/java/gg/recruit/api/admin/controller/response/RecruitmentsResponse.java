package gg.recruit.api.admin.controller.response;

import java.util.ArrayList;
import java.util.List;

import gg.data.recruit.recruitment.Recruitment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentsResponse {
	private List<RecruitmentDto> recruitmentDtoList = new ArrayList<>();

	public RecruitmentsResponse(List<Recruitment> recruitments) {
		for (Recruitment recruitment : recruitments) {
			recruitmentDtoList.add(RecruitmentDto.toRecruitmentDto(recruitment));
		}
	}
}
