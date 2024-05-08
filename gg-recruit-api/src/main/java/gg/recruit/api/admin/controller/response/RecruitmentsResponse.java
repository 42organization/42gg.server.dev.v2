package gg.recruit.api.admin.controller.response;

import java.util.ArrayList;
import java.util.List;

import gg.data.recruit.recruitment.Recruitment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentsResponse {
	private List<RecruitmentDto> recruitments = new ArrayList<>();
	private int totalPage;

	public RecruitmentsResponse(List<Recruitment> recruitmentList, int totalPage) {
		for (Recruitment recruitment : recruitmentList) {
			this.recruitments.add(RecruitmentDto.toRecruitmentDto(recruitment));
		}
		this.totalPage = totalPage;
	}
}
