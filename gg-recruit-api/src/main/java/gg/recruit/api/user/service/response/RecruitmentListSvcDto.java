package gg.recruit.api.user.service.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.Recruitment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentListSvcDto {
	private List<RecruitmentSvcDto> recruitments;
	private Integer totalPage;

	public RecruitmentListSvcDto(List<Recruitment> recruitments, Integer totalPage) {
		this.recruitments = recruitments.stream().map(RecruitmentSvcDto::new).collect(Collectors.toList());
		this.totalPage = totalPage;
	}
}
