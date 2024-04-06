package gg.recruit.api.admin.service.result;

import java.util.List;

import gg.data.recruit.recruitment.Recruitment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllRecruitmentsResult {
	private int totalPage;
	private List<Recruitment> allRecruitments;

	public AllRecruitmentsResult(int totalPage, List<Recruitment> allRecruitments) {
		this.totalPage = totalPage;
		this.allRecruitments = allRecruitments;
	}
}
